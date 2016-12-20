package app.bitrader

import android.support.v4.app.Fragment
import app.bitrader.activity.fragments.PairsListFragment
import app.bitrader.api.ApiProvider
import app.bitrader.api.bitfinex.Bitfinex
import app.bitrader.api.common._
import app.bitrader.api.poloniex._
import com.github.nscala_time.time.Imports._
import diode._
import macroid.FragmentBuilder
import macroid.FullDsl._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Alex Afanasev
  */
trait ICircuit extends Circuit[RootModel] {
  def serviceData: ModelRW[RootModel, ServiceData]

  def serviceContext: ModelRW[RootModel, ApiContext]

  def dataSubscribe[Data <: AnyRef](get: ServiceData => Data)(listen: Data => Unit): () => Unit
}

class AppCircuit extends ICircuit {

  def initialModel = RootModel()

  private def selectedAccount: ModelRW[RootModel, Account] = zoomRW(_.selectedAccount)((model: RootModel, account: Account) =>
    model.copy(selectedAccount = model.selectedAccount))

  private def apiFacade = AppContext.getService(selectedAccount.zoom(_.api).value)

  def serviceContext: ModelRW[RootModel, ApiContext] = selectedAccount.zoomRW(
    _.context)((m, newServiceContext) => m.copy(context = newServiceContext))

  def serviceData: ModelRW[RootModel, ServiceData] = serviceContext
    .zoomRW(_.serviceData)((m, v) => m.copy(serviceData = v))

  def dataSubscribe[Data <: AnyRef](get: ServiceData => Data)(listen: Data => Unit) =
    subscribe(serviceData.zoom(get))(m => listen(m.value))

  val selectApi = new ActionHandler(zoomRW(_.selectedAccount)((m, v) => m.copy(selectedAccount = v))) {
    override protected def handle = {
      case SelectAccount(api) => updated(api)
    }
  }

  val updateAccounts = new ActionHandler(zoomRW(_.accounts)((model: RootModel, accounts: Seq[Account]) =>
    model.copy(accounts = accounts))) {
    override protected def handle = {
      case AddAccount(a) => updated(a +: value)
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

  private val wampSubscription = new ActionHandler(serviceData) {
    override protected def handle = {
      case SubscribeToOrders(sub) =>
        apiFacade(_.wampSubscribe[sub.WampSubType](sub))
        noChange

      case CloseWampChannel =>
        apiFacade(_.wampClose)
        noChange
    }
  }

  private val orderBookList = new ActionHandler(
    serviceData.zoomRW(_.orderBook)((m, v) => m.copy(orderBook = v))
      .zoomRW(_.orders)((m, v) => m.copy(orders = v))
  ) {
    override def handle = {
      case UpdateOrderBook(cp) =>
        effectOnly(Effect(apiFacade(_.ordersBook(cp, 20)).map(ReceiveOrderBook)))
      case ReceiveOrderBook(ob: OrdersBook) => updated(ob)
    }
  }

  private val chartsHandler = new ActionHandler(serviceData.zoomRW(_.chartsData)((m, v) => m.copy(chartsData = v))) {
    override def handle = {
      case UpdateCharts(cp) =>
        val request: Future[Seq[Chart]] = apiFacade(
          _.chartData(cp, 5.hours.ago().unixtime, DateTime.now.unixtime, 300)
        )
        effectOnly(Effect(request.map(r => ChartsUpdated(r))))
      case ChartsUpdated(c) => updated(c)
    }
  }

  private val currenciesHandler = new ActionHandler(serviceData.zoomRW(_.currencies)((m, v) => m.copy(currencies = v))) {
    override def handle = {
      case UpdateCurrencies =>
        val request: Future[Map[String, Currency]] = apiFacade(
          _.currencies()
        )
        effectOnly(Effect(request.map(r => CurrenciesUpdated(r))))
      case CurrenciesUpdated(c) => updated(c)
    }
  }

  private val balancesHandler = new ActionHandler(serviceData.zoomRW(_.balance)((m, v) => m.copy(balance = v))) {
    override def handle = {
      case UpdateBalances =>
        val request: Future[BalancesList] = apiFacade(_.balances)
        def hasMoney(a: BalancesList): BalancesList = a.filter {
          case (coin, amout) => amout.toDouble > 0
        }

        effectOnly(Effect(request.map(r => BalancesUpdated(hasMoney(r)))))
      case BalancesUpdated(c) => updated(Some(c))
    }
  }


  private val uiStateHandler = new ActionHandler(zoomRW(_.uiState)((m, v) => m.copy(uiState = v))) {
    override def handle = {
      case SetMainFragment(f) => updated(UiState(Option(f)))
    }
  }

  override val actionHandler: HandlerFunction = composeHandlers(
    orderBookUpdatesHandler, orderBookList, uiStateHandler, balancesHandler,
    chartsHandler, currenciesHandler, wampSubscription, selectApi, updateAccounts
  )
}


// root model

case class RootModel(
                      selectedAccount: Account = AppContext.accounts.head,
                      uiState: UiState = UiState(),
                      accounts: Seq[Account] = AppContext.accounts
                    )

case class Account(api: ApiProvider, context: ApiContext) {
  def name: String = api.toString
}

case class UiState(
                    mainFragment: Option[FragmentBuilder[_ <: Fragment]] = None
                  )

case class ApiContext(
                           theme: Int,
                           auth: UserProfile = UserProfile(),
                           serviceData: ServiceData = ServiceData()
                         )

case class ServiceData(
                        orderBook: OrderBookContainer =
                        OrderBookContainer(
                          orders = OrdersBook(Seq.empty, Seq.empty, "0", 0),
                          changes = Seq.empty),
                        balance: Option[BalancesList] = None,
                        currencies: Map[String, Currency] = Map.empty,
                        chartsData: Seq[Chart] = Seq.empty
                      )

