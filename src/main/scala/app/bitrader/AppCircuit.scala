package app.bitrader

import app.bitrader.api.poloniex.{CurrencyPair, OrdersBook}
import diode.{ActionHandler, Circuit, Effect}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Alex Afanasev
  */
object AppCircuit extends Circuit[RootModel]  {

  def initialModel = RootModel()

  val orderBookUpdatesHandler = new ActionHandler(zoomRW(_.orderBook.changes)((m, v) =>
    m.copy(orderBook = m.orderBook.copy(changes = v)))) {
    override def handle = {
      // todo handle subscription case here
      //      case ResetWampMessages[OrderWampMsg] =>

      case AddWampMessages(ms: Seq[OrderWampMsg]) =>
        //        println(s"AddMessages OrderWampMsg $ms")
        updated(value ++ ms)
    }
  }

  val orderBookList = new ActionHandler(zoomRW(_.orderBook.orders)((m, v) =>
    m.copy(orderBook = m.orderBook.copy(orders = v)))) {
    override def handle = {
      case UpdateOrderBook(cp) =>
        val service: Future[OrdersBook] = APIContext.poloniexService(_.ordersBook(cp, 20))
        effectOnly(Effect(service.map(ReceiveOrderBook)))
      case ReceiveOrderBook(ob: OrdersBook) => updated(ob)
    }
  }

  override val actionHandler = composeHandlers(orderBookUpdatesHandler, orderBookList)
}

case class RootModel(orderBook: OrderBookContainer =
                     OrderBookContainer(
                       orders = OrdersBook(Seq.empty, Seq.empty, 0, 0),
                       changes = Seq.empty),
                     user: UserProfile = UserProfile(
                       None,
                       Some(AuthData("",""))
                     ))

// actions

case class UpdateOrderBook(cp: CurrencyPair)

case class ReceiveOrderBook(ob: OrdersBook)

case class AddWampMessage[T <: WampMsg](m: T)

case class AddWampMessages[T <: WampMsg](ms: Seq[T])

case class ResetWampMessages[T <: WampMsg]()

case class SubscribeToChannel(t: String)

case class UnsubscribeFromChannel(t: String)