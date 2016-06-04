package app.bitrader.api.poloniex

import android.content.Context
import app.bitrader.TR
import app.bitrader.api.common.CurrencyPair.CurrencyPair
import app.bitrader.api.common.{CurrencyPair, WampMsg}
import app.bitrader.api.network.{AuthInterceptor, WampSub}
import app.bitrader.api.{AbstractFacade, ApiProvider}
import scala.collection.JavaConverters._

/**
  * Created by Alex Afanasev
  */
case object Poloniex extends ApiProvider {
  override type ApiFacade = PoloniexFacade

  override def facade(implicit ctx: Context) = new PoloniexFacade
}


class PoloniexFacade(implicit ctx: Context) extends AbstractFacade  {

  override type PublicApi = PoloniexPublicAPI
  override type PrivateApi = PoloniexTradingAPI

  override val wampApi: WampApi = buildWamp(TR.string.poloniex_wamp.value)
  override val publicApi: PublicApi = buildApi[PublicApi](TR.string.poloniex_url.value)
  override val privateApi: PrivateApi = buildApi[PrivateApi](TR.string.poloniex_trading_url.value,
    _.interceptors().add(new AuthInterceptor(ctx)))

  def ordersBook(pair: CurrencyPair, depth : Int) : OrdersBook = publicApi.ordersBook(pair, depth)

  def ordersBook(depth : Int) : Map[String, OrdersBook] = publicApi.ordersBook(depth)

  def tradeHistory(pair: CurrencyPair) : Seq[TradeHistory] = publicApi.tradeHistory(pair)

  def chartData(pair: CurrencyPair, start: Long, end: Long, period: Int): Seq[Chart] = publicApi.chartData(pair, start, end, period)

  def currencies() : Map[String, Currency] = publicApi.currencies()

  override def wampSubscribe[WM <: WampMsg : scala.reflect.Manifest](sub: WampSub[WM]): Unit = wampApi.openSubscription[WM](sub)

  override def wampClose: Unit = wampApi.closeConnection()

  // private

  def params(command: String, params: Map[String, String] = Map.empty) = {
    (Map("nonce" -> nonce, "command" -> command) ++ params).asJava
  }

  def balances: Map[String, String] = privateApi.post(params("returnBalances"))

  def myTradeHistory(currencyPair: String = "all") = privateApi.returnTradeHistory(params("returnTradeHistory", Map("currencyPair" -> currencyPair)))

  def returnOpenOrders(currencyPair: String = "all") = privateApi.returnOpenOrders(params("returnOpenOrders", Map("currencyPair" -> currencyPair)))
}
