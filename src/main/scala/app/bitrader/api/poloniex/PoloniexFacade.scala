package app.bitrader.api.poloniex

import android.content.Context
import app.bitrader.TR
import app.bitrader.api.{AbstractFacade, ApiService}
import app.bitrader.api.network.AuthInterceptor
import retrofit.http.Query

/**
  * Created by Alex Afanasev
  */
case object Poloniex extends ApiService {
  override type ApiFacade = PoloniexFacade

  override def facade(implicit ctx: Context) = new PoloniexFacade
}


class PoloniexFacade(implicit ctx: Context) extends AbstractFacade  {

  override type PublicApi = PoloniexPublicAPI
  override type PrivateApi = PoloniexTradingAPI
  override type WampApi = PoloniexWampApi

  override val wampApi = null
  override val publicApi: PublicApi = buildApi[PublicApi](TR.string.poloniex_url.value)
  override val privateApi: PrivateApi = buildApi[PrivateApi](TR.string.poloniex_trading_url.value,
    _.interceptors().add(new AuthInterceptor(ctx)))

  def ordersBook(pair: CurrencyPair, depth : Int) : OrdersBook = publicApi.ordersBook(pair, depth)

  def ordersBook(depth : Int) : Map[String, OrdersBook] = publicApi.ordersBook(depth)

  def tradeHistory(pair: CurrencyPair) : Seq[TradeHistory] = publicApi.tradeHistory(pair)

  def chartData(pair: CurrencyPair, start: Long, end: Long, period: Int): Seq[Chart] = publicApi.chartData(pair, start, end, period)

  def currencies() : Map[String, Currency] = publicApi.currencies()

  def balances: Map[String, String] = privateApi.get(nonce, "returnBalances")

}
