package app.bitrader.activity

import java.util.concurrent.TimeUnit

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{View, ViewGroup}
import android.widget.{LinearLayout, TextView}
import app.bitrader._
import app.bitrader.api.poloniex.{CurrencyPair, OrdersBook}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks
import diode.Dispatcher
import diode.data.{Fetch, PotStream}
import io.github.aafa.helpers.{Styles, UiThreading}
import macroid.FullDsl._
import macroid.{ContextWrapper, Contexts, Ui, _}
import rx.{Observer, Subscription}
import ws.wamp.jawampa.WampClient.{ConnectedState, State}
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider
import ws.wamp.jawampa.{PubSubData, WampClient, WampClientBuilder}

import scala.collection.SortedMap

/**
  * Created by Alex Afanasev
  */
class WampActivity extends Activity with Contexts[Activity] {

  private val appCircuit = AppCircuit
  lazy val view = new WampView(appCircuit)
  lazy val javampa = new JawampaClient(AppCircuit)
  lazy val modifyOrders = AppCircuit.subscribe(AppCircuit.zoom(_.orderBook.changes))(m => view.modifyOrders(m.value))
  lazy val ordersList = AppCircuit.subscribe(AppCircuit.zoom(_.orderBook.orders))(m => view.updateOrdersList(m.value))

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(view.ui)
    appCircuit(UpdateOrderBook(CurrencyPair.BTC_ETH))

    javampa.connect()
    modifyOrders
    ordersList
  }

  override def onPause(): Unit = {
    super.onPause()

    javampa.closeConnection()
    modifyOrders.apply()
    ordersList.apply()
  }
}

class CustomLinearLayoutManager(implicit cw: ContextWrapper) extends LinearLayoutManager(cw.bestAvailable){

  override def onLayoutChildren(recycler: RecyclerView#Recycler, state: RecyclerView.State): Unit = {
    try {
      super.onLayoutChildren(recycler, state)
    } catch {
      case e: IndexOutOfBoundsException => println(s"got ioobe $e")
    }
  }

}

class WampView(dispatcher: Dispatcher)(implicit c: ContextWrapper) extends Styles with UiThreading {

  import RecyclerViewTweaks._

  var modifyStream = Seq.empty[OrderWampMsg]
  val asksAdapter = new MessagesAdapter()
  val bidsAdapter = new MessagesAdapter(true)

  def rv: Ui[RecyclerView] = w[RecyclerView] <~
    rvFixedSize <~
    rvLayoutManager(new CustomLinearLayoutManager)


  val ui: View = {
    l[LinearLayout](
      rv <~ rvAdapter(asksAdapter) <~ vWrap,
      rv <~ rvAdapter(bidsAdapter) <~ vWrap
    ) <~ vMatchParent
  }.get

  def processModifications(s: Seq[OrderWampMsg]) = s map { owm => {
    owm.process(new OrderProcessing {
      def bidModify(o: OrderPair) = bidsAdapter.updateOrder(o)

      def askModify(o: OrderPair) = asksAdapter.updateOrder(o)

      def askRemove(o: OrderPair) = asksAdapter.removeOrder(o)

      def bidRemove(o: OrderPair) = bidsAdapter.removeOrder(o)

      def bidNew(o: OrderPair): Unit = bidsAdapter.addOrder(o)

      def askNew(o: OrderPair): Unit = asksAdapter.addOrder(o)
    })
  }
  }


  def modifyOrders(s: Seq[OrderWampMsg]) = runOnUiThread {
    processModifications(s.filterNot(modifyStream.contains))
    modifyStream = s
  }

  def updateOrdersList(ob: OrdersBook) = {
    asksAdapter.updateOrderList(ob.asksMap)
    bidsAdapter.updateOrderList(ob.bidsMap)
  }


}


class MessagesAdapter(val reverse: Boolean = false, size: Int = 20)(implicit context: ContextWrapper)
  extends RecyclerView.Adapter[ViewHolder] with UiThreading {

  var orders: OrdersMap = SortedMap.empty

  def updateOrderList(os: OrdersMap) = runOnUiThread {
    orders = os
    this.notifyDataSetChanged()
  }

  def updateOrder(o: OrderPair) = o.change{ p =>
    val (updateKey, updateValue) = o
    orders = orders.map {
      case (k, v) if k == updateKey =>
        (updateKey, updateValue)
      case any => any
    }

    this.notifyItemChanged(p)
  }


  def addOrder(o: OrderPair) = runOnUiThread {
    orders += o
    notifyItemInserted(o.pos)
  }

  def removeOrder(o: OrderPair) = o.change{ p =>
    orders = orders.filterNot { case (k, v) => k == o._1 }
    this.notifyItemRemoved(p)
  }


  override def getItemCount: Int = list.size

  def list: Seq[OrderPair] = {
    var seq = orders.to[Seq]
    if (reverse) {
      seq = seq.reverse
    }
    seq
  }

  override def onBindViewHolder(vh: ViewHolder, i: Int): Unit = {
    val (k, v) = list(i)
    Ui.run(
      vh.title <~ text("%s   %.3f".format(k, v))
    )
  }

  override def onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder = {
    ViewHolder(new WampItemAdapter())
  }


  implicit class Position(o: OrderPair) {
    def change(found: Int => Unit): Unit = runOnUiThread {
      val indexOf: Int = o.pos
      if (indexOf > -1) found(indexOf)
    }

    def pos = orders.keys.toIndexedSeq.indexOf(o._1)
  }

}

class WampItemAdapter(implicit context: ContextWrapper) {
  var title = slot[TextView]

  private val content: TextView = {
    w[TextView] <~ wire(title) <~ padding(all = 2.dp)
  }.get

  def layout = content
}

case class ViewHolder(adapter: WampItemAdapter)(implicit context: ContextWrapper)
  extends RecyclerView.ViewHolder(adapter.layout) {

  val content = adapter.layout

  val title = adapter.title

}

class JawampaClient(dispatcher: Dispatcher)(implicit ctx: ContextWrapper) {

  val wsuri = "wss://api.poloniex.com"

  class TickerData(currencyPair: String, last: BigDecimal, lowestAsk: BigDecimal, highestBid: BigDecimal,
                   percentChange: BigDecimal, baseVolume: BigDecimal, quoteVolume: BigDecimal, isFrozen: Byte,
                   dayHigh: BigDecimal, dayLow: BigDecimal) {
    override def toString: String = s"Got $currencyPair for $last"
  }

  val wamp: WampClient = buildWamp()

  def closeConnection() = {
    wamp.close()
  }

  def connect() = {
    onConnected(() => {
      wampSubscription[OrderWampMsg](CurrencyPair.BTC_ETH.toString)
    }
    )
    wamp.open()
  }

  private def onConnected(subs: () => Unit): Subscription = {
    wamp.statusChanged().subscribe(new Observer[State] {
      override def onCompleted(): Unit = {
        println("statusChanged onCompleted")
      }

      override def onError(e: Throwable): Unit = println(s"statusChanged onError $e")

      override def onNext(t: State): Unit = t match {
        case c: ConnectedState =>
          println(s"next status $t")
          subs()
        case _ => println(s"next status $t")
      }
    })
  }

  private def wampSubscription[WM <: WampMsg : scala.reflect.Manifest](topic: String): PotStream[Long, String] = {
    val stream = PotStream[Long, String](new NoopFetch)
    wamp.makeSubscription(topic).subscribe(
      new Observer[PubSubData] {
        override def onCompleted(): Unit = println(s"$topic sub onCompleted")

        override def onError(e: Throwable): Unit = println(s"$topic sub onError $e")

        override def onNext(t: PubSubData): Unit = {
          val value: Seq[WM] = APIContext.jacksonMapper.readValue[Seq[WM]](t.arguments().toString)
          dispatcher(AddWampMessages[WM](value))
        }
      })
    stream
  }

  def buildWamp(): WampClient = {
    new WampClientBuilder()
      .withConnectorProvider(new NettyWampClientConnectorProvider)
      .withUri(wsuri)
      .withRealm("realm1")
      .withInfiniteReconnects()
      .withReconnectInterval(5, TimeUnit.SECONDS)
      .build()
  }

}

class NoopFetch extends Fetch[Long] {
  override def fetch(key: Long): Unit = {}

  override def fetch(keys: Traversable[Long]): Unit = {}
}
