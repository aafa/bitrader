package app.bitrader.api.bitfinex

/**
  * Created by Alex Afanasev
  */

import android.content.Context
import app.bitrader.TR
import app.bitrader.api.common.CurrencyPair.CurrencyPair
import app.bitrader.api.common.{CurrencyPair, WampMsg}
import app.bitrader.api.network.{AuthInterceptor, WampSub}
import app.bitrader.api.poloniex.{Chart, OrdersBook, TradeHistory}
import app.bitrader.api.{AbstractFacade, ApiProvider}

/**
  * Created by Alex Afanasev
  */
case object Bitfinex extends ApiProvider {
  override type ApiFacade = BitfinexFacade

  override def facade(implicit ctx: Context) = new BitfinexFacade
}


class BitfinexFacade(implicit ctx: Context) extends AbstractFacade  {

  override type PublicApi = BitfinexPublicAPI
  override type PrivateApi = BitfinexPrivateAPI

  override val wampApi: WampApi = buildWamp(TR.string.bitfinex_wamp.value)
  override val publicApi: PublicApi = buildApi[PublicApi](TR.string.bitfinex_url.value)
  override val privateApi: PrivateApi = buildApi[PrivateApi](TR.string.bitfinex_trading_url.value,
    _.interceptors().add(new AuthInterceptor(ctx)))

  override def chartData(pair: CurrencyPair, start: Long, end: Long, period: Int): Seq[Chart] = Seq.empty

  override def wampSubscribe[WM <: WampMsg : Manifest](subs: WampSub[WM]): Unit = ???

  override def wampClose: Unit = ???

  override def ordersBook(pair: CurrencyPair, depth: Int): OrdersBook = ???

  override def tradeHistory(pair: CurrencyPair): Seq[TradeHistory] = ???
}
