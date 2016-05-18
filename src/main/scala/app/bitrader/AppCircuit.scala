package app.bitrader

import android.content.Context
import app.bitrader.api.ApiService
import app.bitrader.api.poloniex._
import com.github.nscala_time.time.Imports._
import diode.ActionResult.{EffectOnly, ModelUpdate}
import diode._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Alex Afanasev
  */
object AppCircuit extends Circuit[RootModel] {

  lazy val appContext: Context = APIContext.appContext

  def initialModel = RootModel()

  private def apiService: ApiService = zoom(_.selectedApi).value

  def serviceContext: ModelRW[RootModel, ServiceContext] = zoomRW(_.serviceContext(apiService))((m, newServiceContext) => m.copy(serviceContext =
    m.serviceContext.map {
      case (k, v) if k == apiService => (apiService, newServiceContext)
      case a => a
    }))

  def serviceData: ModelRW[RootModel, ServiceData] = serviceContext.zoomRW(_.serviceData)((m, v) => m.copy(serviceData = v))

  val orderBookUpdatesHandler = new ActionHandler(
    zoomRW(_.orderBook)((m, v) => m.copy(orderBook = v))
      .zoomRW(_.changes)((m, v) => m.copy(changes = v))
  ) {
    override def handle = {
      // todo handle subscription case here
      //      case ResetWampMessages[OrderWampMsg] =>

      case AddWampMessages(ms: Seq[OrderWampMsg]) =>
        //        println(s"AddMessages OrderWampMsg $ms")
        updated(value ++ ms)
    }
  }

  val orderBookList = new ActionHandler(
    zoomRW(_.orderBook)((m, v) => m.copy(orderBook = v))
      .zoomRW(_.orders)((m, v) => m.copy(orders = v))
  ) {
    override def handle = {
      case UpdateOrderBook(cp) =>
        val service: Future[OrdersBook] = APIContext.poloniexService(_.ordersBook(cp, 20))
        effectOnly(Effect(service.map(ReceiveOrderBook)))
      case ReceiveOrderBook(ob: OrdersBook) => updated(ob)
    }
  }

  val uiUpdates: HandlerFunction = (model, action) => action match {
    case ChartsUpdated(api, c) =>
      val chartsZoom: ModelRW[RootModel, Seq[Chart]] = serviceData.zoomRW(_.chartsData)((m, v) => m.copy(chartsData = v))
      Some(ModelUpdate(chartsZoom.updated(c)))
    case _ => None
  }

  val apiRequest: HandlerFunction = (model, action) => action match {
    case UpdateCharts(api) =>
      val request: Future[Seq[Chart]] = APIContext.poloniexService(
        _.chartData(CurrencyPair.BTC_ETH, 5.hours.ago().unixtime, DateTime.now.unixtime, 300)
      )
      val effect: EffectSingle[ChartsUpdated] = Effect(request.map(r => ChartsUpdated(api, r)))
      Some(EffectOnly(effect))
    case _ => None
  }

  override val actionHandler = composeHandlers(orderBookUpdatesHandler, orderBookList, uiUpdates, apiRequest)
}

class ApiData(m: ModelRW[RootModel, ServiceData]) extends ActionHandler(m)
{
  override protected def handle = {
    case _ => noChange
  }
}

// model

case class RootModel(orderBook: OrderBookContainer =
                     OrderBookContainer(
                       orders = OrdersBook(Seq.empty, Seq.empty, 0, 0),
                       changes = Seq.empty),

                     selectedApi: ApiService = Poloniex,
                     serviceContext: Map[ApiService, ServiceContext] = Map(
                       Poloniex -> ServiceContext()
                     )
                    )


case class ServiceContext(
                           auth: UserProfile = UserProfile(),
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

sealed trait ApiAction

case class UpdateCharts(api: ApiService) extends ApiAction

case class ChartsUpdated(api: ApiService, c: Seq[Chart]) extends ApiAction


