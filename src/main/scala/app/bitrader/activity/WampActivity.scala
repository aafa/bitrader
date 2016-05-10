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

class WampView(dispatcher: Dispatcher)(implicit c: ContextWrapper) extends Styles {

  import RecyclerViewTweaks._

  var modifyStream = Seq.empty[OrderWampMsg]
  lazy val asksAdapter: MessagesAdapter = new MessagesAdapter(dispatcher)
  lazy val bidsAdapter: MessagesAdapter = new MessagesAdapter(dispatcher)

  def rv: Ui[RecyclerView] = w[RecyclerView] <~
    rvFixedSize <~
    rvLayoutManager(new LinearLayoutManager(c.bestAvailable))


  val ui: View = {
    l[LinearLayout](
      rv <~ rvAdapter(asksAdapter) <~ vWrap,
      rv <~ rvAdapter(bidsAdapter) <~ vWrap
    ) <~ vMatchParent
  }.get

  def processModifications(s: Seq[OrderWampMsg]) = s map { owm => {
    owm.process(new owm.OrderProcessing {
      def bidModify(o: OrderPair) = bidsAdapter.updateOrder(o)

      def askModify(o: OrderPair) = asksAdapter.updateOrder(o)

      def askRemove(o: BigDecimal) = asksAdapter.removeOrder(o)

      def bidRemove(o: BigDecimal) = bidsAdapter.removeOrder(o)

      override def bidNew(o: (BigDecimal, BigDecimal)): Unit = {}

      override def askNew(o: (BigDecimal, BigDecimal)): Unit = {}
    })
  }
  }


  def modifyOrders(s: Seq[OrderWampMsg]) = {
    processModifications(s.filterNot(modifyStream.contains))
    modifyStream = s
  }

  def updateOrdersList(ob: OrdersBook) = {
    asksAdapter.updateOrderList(ob.asks)
    bidsAdapter.updateOrderList(ob.bids)
  }


}


class MessagesAdapter(dispatcher: Dispatcher)
                     (implicit context: ContextWrapper)
  extends RecyclerView.Adapter[ViewHolder] with UiThreading {

  var orders = Map.empty[BigDecimal, BigDecimal]

  def updateOrderList(os: Seq[(BigDecimal, BigDecimal)]) = runOnUiThread {
    orders = os.toMap
    this.notifyDataSetChanged()
  }

  def updateOrder(o: (BigDecimal, BigDecimal)) = runOnUiThread {
    val (updateKey, updateValue) = o
    if (orders.keySet.contains(updateKey)) {
      val indexOf: Int = orders.keys.toIndexedSeq.indexOf(updateKey)

      orders = orders.collect {
        case (k, v) if k == updateKey =>
          (updateKey, updateValue)
      }

      this.notifyItemChanged(indexOf)
    }
  }

  def removeOrder(o: BigDecimal) = runOnUiThread {
    val indexOf: Int = orders.keys.toIndexedSeq.indexOf(o)
    orders = orders.filterNot { case (k, v) => k == o }
    this.notifyItemRemoved(indexOf)
  }

  override def getItemCount: Int = orders.size

  override def onBindViewHolder(vh: ViewHolder, i: Int): Unit = {
    val (k, v) = orders.to[Seq].apply(i)
    Ui.run(
      vh.title <~ text("%s   %.2f".format(k, v))
    )
  }

  override def onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder = {
    ViewHolder(new WampItemAdapter())
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
