package app.bitrader.api.poloniex

import android.content.Context
import app.bitrader.TR
import app.bitrader.api.common.CurrencyPair.CurrencyPair
import app.bitrader.api.common.WampMsg
import app.bitrader.api.network.{AuthInterceptor, JawampaClient, WampSub}
import app.bitrader.api.{AbstractFacade, ApiProvider}
import okhttp3.OkHttpClient

import scala.collection.JavaConverters._

/**
  * Created by Alex Afanasev
  */
case object Poloniex extends ApiProvider {
  override type ApiFacade = PoloniexFacade

  override def facade(implicit ctx: Context) = new PoloniexFacade
}


class PoloniexFacade(implicit ctx: Context) extends AbstractFacade {

  override val wampApi: JawampaClient = buildWamp(TR.string.poloniex_wamp.value)
  override val publicApi: PoloniexOkAPI = new PoloniexOkAPI(TR.string.poloniex_url.value)
  override val privateApi: PoloniexOkTradingAPI = new PoloniexOkTradingAPI(TR.string.poloniex_trading_url.value)

  def returnTicket: Map[String, Ticker] = publicApi.returnTicker()

  def ordersBook(pair: CurrencyPair, depth: Int): OrdersBook = publicApi.ordersBook(pair, depth)

  def ordersBook(depth: Int): Map[String, OrdersBook] = publicApi.ordersBook(depth)

  def tradeHistory(pair: CurrencyPair): Seq[TradeHistory] = publicApi.tradeHistory(pair)

  def chartData(pair: CurrencyPair, start: Long, end: Long, period: Int): Seq[Chart] = publicApi.chartData(pair, start, end, period)

  def currencies(): Map[String, Currency] = publicApi.currencies()

  override def wampSubscribe[WM <: WampMsg : scala.reflect.Manifest](sub: WampSub[WM]): Unit = wampApi.openSubscription[WM](sub)

  override def wampClose: Unit = wampApi.closeConnection()

  // private

  def balances: Map[String, String] = privateApi.balances()

  def myTradeHistory(currencyPair: String = "all") = privateApi.returnTradeHistory(currencyPair)

  def returnOpenOrders(currencyPair: String = "all") = privateApi.returnOpenOrders(currencyPair)

  def buy(currencyPair: String, rate: Double, amount: Double) = privateApi.buy(currencyPair, rate, amount)

  def sell(currencyPair: String, rate: Double, amount: Double) = privateApi.sell(currencyPair, rate, amount)

  def cancelOrder(orderNumber: String) = privateApi.cancelOrder(orderNumber)

  def returnDepositAddresses = privateApi.returnDepositAddresses()

  def returnCompleteBalances = privateApi.returnCompleteBalances()

}
