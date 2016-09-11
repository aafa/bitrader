package app.bitrader

import app.bitrader.api.{ApiProvider, apitest}
import app.bitrader.api.bitfinex.Bitfinex
import app.bitrader.api.common._
import app.bitrader.api.poloniex._
import com.github.nscala_time.time.Imports._
import diode._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Alex Afanasev
  */
object AppCircuit extends Circuit[RootModel] {

  def initialModel = RootModel()

  private def api: ApiProvider = zoom(_.selectedApi).value
  private def apiFacade = APIContext.getService(api)

  def serviceContext: ModelRW[RootModel, ServiceContext] = zoomRW(
    _.serviceContext(api))((m, newServiceContext) => m.copy(serviceContext =
    m.serviceContext.map {
      case (k, v) if k == api => (api, newServiceContext)
      case a => a
    }))

  def serviceData: ModelRW[RootModel, ServiceData] = serviceContext
    .zoomRW(_.serviceData)((m, v) => m.copy(serviceData = v))

  val selectApi = new ActionHandler(zoomRW(_.selectedApi)((m,v) => m.copy(selectedApi = v))) {
    override protected def handle = {
      case SelectApi(api) => updated(api)
    }
  }

  val orderBookUpdatesHandler = new ActionHandler(
    serviceData.zoomRW(_.orderBook)((m, v) => m.copy(orderBook = v))
      .zoomRW(_.changes)((m, v) => m.copy(changes = v))
  ) {
    override def handle = {
      case AddWampMessages(ms: Seq[OrderWampMsg]) =>
        updated(value ++ ms)
    }
  }

  val wampSubscription = new ActionHandler(serviceData) {
    override protected def handle = {
      case SubscribeToOrders(sub) =>
        apiFacade(_.wampSubscribe[sub.WampSubType](sub))
        noChange

      case CloseWampChannel =>
        apiFacade(_.wampClose)
        noChange
    }
  }

  val orderBookList = new ActionHandler(
    serviceData.zoomRW(_.orderBook)((m, v) => m.copy(orderBook = v))
      .zoomRW(_.orders)((m, v) => m.copy(orders = v))
  ) {
    override def handle = {
      case UpdateOrderBook(cp) =>
        effectOnly(Effect(apiFacade(_.ordersBook(cp, 20)).map(ReceiveOrderBook)))
      case ReceiveOrderBook(ob: OrdersBook) => updated(ob)
    }
  }

  val uiUpdates = new ActionHandler(serviceData.zoomRW(_.chartsData)((m, v) => m.copy(chartsData = v))) {
    override def handle = {
      case ChartsUpdated(c) => updated(c)
    }
  }

  val apiRequest = new ActionHandler(serviceData) {
    override protected def handle = {
      case UpdateCharts(cp) =>
        val request: Future[Seq[Chart]] = apiFacade(
          _.chartData(cp, 5.hours.ago().unixtime, DateTime.now.unixtime, 300)
        )
        val effect: EffectSingle[ChartsUpdated] = Effect(request.map(r => ChartsUpdated(r)))
        effectOnly(effect)
    }
  }

  override val actionHandler = composeHandlers(
    orderBookUpdatesHandler, orderBookList,
    uiUpdates, apiRequest, wampSubscription, selectApi
  )

  def dataSubscribe[Data <: AnyRef](get: ServiceData => Data)(listen: Data => Unit) =
    subscribe(serviceData.zoom(get))(m => listen(m.value))
}


// root model

case class RootModel(
                      selectedApi: ApiProvider = Poloniex,
                      serviceContext: Map[ApiProvider, ServiceContext] = Map(
                        Poloniex -> ServiceContext(theme = R.style.MainTheme),
                        Bitfinex -> ServiceContext(theme = R.style.GreenTheme)
                      )
                    )


case class ServiceContext(
                           theme: Int,
                           auth: UserProfile = UserProfile(),
                           serviceData: ServiceData = ServiceData()
                         )

case class ServiceData(
                        orderBook: OrderBookContainer =
                        OrderBookContainer(
                          orders = OrdersBook(Seq.empty, Seq.empty, "0", 0),
                          changes = Seq.empty),
                        currencies: Map[String, Currency] = Map.empty,
                        chartsData: Seq[Chart] = Seq.empty
                      )

