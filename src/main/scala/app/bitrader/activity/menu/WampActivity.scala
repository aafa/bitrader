package app.bitrader.activity.menu

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{View, ViewGroup}
import android.widget.{LinearLayout, TextView}
import app.bitrader._
import app.bitrader.api.common.{CurrencyPair, OrderProcessing, OrderWampMsg}
import app.bitrader.api.network.{JawampaClient, WampSub}
import app.bitrader.api.poloniex.OrdersBook
import app.bitrader.helpers.{Styles, UiThreading}
import diode.Dispatcher
import macroid.FullDsl._
import macroid.{ContextWrapper, Contexts, Ui, _}

import scala.collection.SortedMap
import scala.collection.mutable.ArrayBuffer

/**
  * Created by Alex Afanasev
  */
class WampActivity extends Activity with Contexts[Activity] {

  private val appCircuit = AppCircuit
  lazy val view = new WampView(appCircuit)
  lazy val modifyOrders = appCircuit.dataSubscribe(_.orderBook.changes)(view.modifyOrders)
  lazy val ordersList = appCircuit.dataSubscribe(_.orderBook.orders)(view.updateOrdersList)
  val currencyToTrack = CurrencyPair.BTC_ETH

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(view.ui)
    appCircuit(UpdateOrderBook(currencyToTrack))

    val sub: WampSub[OrderWampMsg] = WampSub[OrderWampMsg](currencyToTrack.toString)
    appCircuit(SubscribeToOrders(sub))

    modifyOrders
    ordersList
  }

  override def onPause(): Unit = {
    super.onPause()

    appCircuit(CloseWampChannel)
    modifyOrders.apply()
    ordersList.apply()
  }
}


class WampView(dispatcher: Dispatcher)(implicit c: ContextWrapper) extends Styles with UiThreading {

  import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._

  var modifyStream = Seq.empty[OrderWampMsg]
  val asksAdapter = new MessagesAdapter()
  val bidsAdapter = new MessagesAdapter(true)

  def rv: Ui[RecyclerView] = w[RecyclerView] <~
    rvNoFixedSize <~
    rvLayoutManager(new LinearLayoutManager(c.getOriginal))


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

  def modifyOrders(s: Seq[OrderWampMsg]) = {
    this.synchronized {
      processModifications(s.filterNot(modifyStream.contains))
      modifyStream = s
    }
  }

  def updateOrdersList(ob: OrdersBook) = {
    asksAdapter.updateOrderList(ob.asksMap)
    bidsAdapter.updateOrderList(ob.bidsMap)
  }


}


class MessagesAdapter(val reverse: Boolean = false, size: Int = 20)(implicit context: ContextWrapper)
  extends RecyclerView.Adapter[ItemHolder] with UiThreading {

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


  override def getItemCount: Int = orders.size

  def dataArray: Vector[OrderPair] = {
    var seq = orders.to[Vector]
    if (reverse) {
      seq = seq.reverse
    }
    seq
  }

  override def onBindViewHolder(vh: ItemHolder, i: Int): Unit = {
    val (k, v) = dataArray(i)
    Ui.run(
      vh.title <~ text("%s   %.3f".format(k, v))
    )
  }

  override def onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemHolder = {
    ItemHolder(new WampItemAdapter())
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

case class ItemHolder(adapter: WampItemAdapter)(implicit context: ContextWrapper)
  extends RecyclerView.ViewHolder(adapter.layout) {

  val content = adapter.layout

  val title = adapter.title

}

