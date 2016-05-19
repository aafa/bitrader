package app.bitrader

import android.content.Context
import app.bitrader.api.ApiService
import app.bitrader.api.common._
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

  def initialModel = RootModel()

  private def apiService: ApiService = zoom(_.selectedApi).value

  def serviceContext: ModelRW[RootModel, ServiceContext] = zoomRW(
    _.serviceContext(apiService))((m, newServiceContext) => m.copy(serviceContext =
    m.serviceContext.map {
      case (k, v) if k == apiService => (apiService, newServiceContext)
      case a => a
    }))

  def serviceData: ModelRW[RootModel, ServiceData] = serviceContext.zoomRW(_.serviceData)((m, v) => m.copy(serviceData = v))

  val orderBookUpdatesHandler = new ActionHandler(
    serviceData.zoomRW(_.orderBook)((m, v) => m.copy(orderBook = v))
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
    serviceData.zoomRW(_.orderBook)((m, v) => m.copy(orderBook = v))
      .zoomRW(_.orders)((m, v) => m.copy(orders = v))
  ) {
    override def handle = {
      case UpdateOrderBook(cp) =>
        val service: Future[OrdersBook] = APIContext.getService(apiService)(_.ordersBook(cp, 20))
        effectOnly(Effect(service.map(ReceiveOrderBook)))
      case ReceiveOrderBook(ob: OrdersBook) => updated(ob)
    }
  }

  val uiUpdates = new ActionHandler(
    serviceData.zoomRW(_.chartsData)((m, v) => m.copy(chartsData = v))
  ) {
    override def handle = {
      case ChartsUpdated(c) => updated(c)
    }
  }

  val apiRequest: HandlerFunction = (model, action) => action match {
    case UpdateCharts =>
      val request: Future[Seq[Chart]] = APIContext.getService(apiService)(
        _.chartData(CurrencyPair.BTC_ETH, 5.hours.ago().unixtime, DateTime.now.unixtime, 300)
      )
      val effect: EffectSingle[ChartsUpdated] = Effect(request.map(r => ChartsUpdated(r)))
      Some(EffectOnly(effect))
    case _ => None
  }

  override val actionHandler = composeHandlers(orderBookUpdatesHandler, orderBookList, uiUpdates, apiRequest)

  def dataSubscribe[Data <: AnyRef](get: ServiceData => Data)(listen: Data => Unit) =
    subscribe(serviceData.zoom(get))(m => listen(m.value))
}


// root model

case class RootModel(
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
                        orderBook: OrderBookContainer =
                        OrderBookContainer(
                          orders = OrdersBook(Seq.empty, Seq.empty, 0, 0),
                          changes = Seq.empty),
                        currencies: Map[String, Currency] = Map.empty,
                        chartsData: Seq[Chart] = Seq.empty
                      )

