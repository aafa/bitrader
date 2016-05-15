package app.bitrader

import app.bitrader.api.{AbstractFacade, ApiService}
import app.bitrader.api.poloniex._
import com.github.nscala_time.time.Imports._
import diode.{ActionHandler, Circuit, Effect}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Alex Afanasev
  */
object AppCircuit extends Circuit[RootModel] {

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

  // todo serviceApi router
  val apiUpdates = new ActionHandler(zoomRW(_.serviceData.chartsData)((model: RootModel, data: Seq[Chart]) =>
    model.copy(serviceData = model.serviceData.copy(chartsData = data))
  )) {
    override def handle = {
      case UpdateCharts(api) =>
        val request: Future[Seq[Chart]] = APIContext.poloniexService(_.chartData(CurrencyPair.BTC_ETH, 5.hours.ago().unixtime, DateTime.now.unixtime, 300))
        effectOnly(Effect(request.map(r => ChartsUpdated(Poloniex, r))))
      case ChartsUpdated(api, c) => updated(c)
    }
  }

  override val actionHandler = composeHandlers(orderBookUpdatesHandler, orderBookList, apiUpdates)
}

// model

case class RootModel(orderBook: OrderBookContainer =
                     OrderBookContainer(
                       orders = OrdersBook(Seq.empty, Seq.empty, 0, 0),
                       changes = Seq.empty),
                     serviceData: ServiceData = ServiceData(),

                     auth: Map[ApiService, UserProfile] = Map(
                       Poloniex -> UserProfile()
                     )
                    )

// todo ApiService -> ServiceContext
case class ServiceContext(
                           apiContext: AbstractFacade,
                           auth: Map[ApiService, UserProfile] = Map(
                             Poloniex -> UserProfile()
                           ),
                           serviceData: ServiceData = ServiceData()
                         )

case class ServiceData(
                        currencies: Map[String, Currency] = Map.empty,
                        chartsData: Seq[Chart] = Seq.empty
                      )

// actions

case class UpdateOrderBook(cp: CurrencyPair)

case class ReceiveOrderBook(ob: OrdersBook)

case class AddWampMessage[T <: WampMsg](m: T)

case class AddWampMessages[T <: WampMsg](ms: Seq[T])

case class ResetWampMessages[T <: WampMsg]()

case class SubscribeToChannel(t: String)

case class UnsubscribeFromChannel(t: String)

case class UpdateCurrencies(api: ApiService)

case class CurrenciesUpdated(api: ApiService)

case class UpdateCharts(api: ApiService)

case class ChartsUpdated(api: ApiService, c: Seq[Chart])
