package app.bitrader.activity.menu

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{View, ViewGroup}
import android.widget.{LinearLayout, TextView}
import app.bitrader._
import app.bitrader.api.network.{JawampaClient, WampSub}
import app.bitrader.api.poloniex.{CurrencyPair, OrdersBook}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks
import diode.Dispatcher
import io.github.aafa.helpers.{Styles, UiThreading}
import macroid.FullDsl._
import macroid.{ContextWrapper, Contexts, Ui, _}

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
  val currencyToTrack = CurrencyPair.BTC_ETH

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(view.ui)
    appCircuit(UpdateOrderBook(currencyToTrack))
    javampa.openSubscription(WampSub[OrderWampMsg](currencyToTrack.toString))
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


class WampView(dispatcher: Dispatcher)(implicit c: ContextWrapper) extends Styles with UiThreading {

  import RecyclerViewTweaks._

  var modifyStream = Seq.empty[OrderWampMsg]
  val asksAdapter = new MessagesAdapter()
  val bidsAdapter = new MessagesAdapter(true)

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

  private object Locker

  def modifyOrders(s: Seq[OrderWampMsg]) = {
    Locker.synchronized {
      processModifications(s.filterNot(modifyStream.contains))
      modifyStream = s
    }
  }

  def updateOrdersList(ob: OrdersBook) =  {
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

  def updateOrder(o: OrderPair) = o.change { p =>
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

  def removeOrder(o: OrderPair) = o.change { p =>
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
