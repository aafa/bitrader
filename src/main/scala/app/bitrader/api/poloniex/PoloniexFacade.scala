package app.bitrader.api.poloniex

import android.content.Context
import app.bitrader.TR
import app.bitrader.api.network.AuthInterceptor
import app.bitrader.api.{API, AbstractFacade}
import retrofit.http.Query

/**
  * Created by Alex Afanasev
  */
class PoloniexFacade(implicit ctx: Context) extends AbstractFacade with API {

  override type PublicApi = PoloniexPublicAPI
  override type PrivateApi = PoloniexTradingAPI
  override type WampApi = PoloniexWampApi

  override val wampApi = null
  override val publicApi: PublicApi = buildApi(TR.string.poloniex_url.value)
  override val privateApi = buildApi(TR.string.poloniex_trading_url.value, { ok =>
    ok.interceptors().add(new AuthInterceptor(ctx))
  })

  def ordersBook(pair: CurrencyPair, depth : Int) : OrdersBook = publicApi.ordersBook(pair, depth)

  def chartData(pair: CurrencyPair, start: Long, end: Long, period: Int): Seq[Chart] = publicApi.chartData(pair, start, end, period)

  def currencies() : Map[String, Currency] = publicApi.currencies()
}
