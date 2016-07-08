package app.bitrader.api.bitfinex

/**
  * Created by Alex Afanasev
  */

import android.content.Context
import app.bitrader.TR
import app.bitrader.api.common.CurrencyPair.CurrencyPair
import app.bitrader.api.common.{CurrencyPair, WampMsg}
import app.bitrader.api.network.{AuthInterceptor, JawampaClient, WampSub}
import app.bitrader.api.poloniex._
import app.bitrader.api.{AbstractFacade, ApiProvider}

/**
  * Created by Alex Afanasev
  */
case object Bitfinex extends ApiProvider {
  override type ApiFacade = BitfinexFacade

  override def facade(implicit ctx: Context) = new BitfinexFacade
}


class BitfinexFacade(implicit ctx: Context) extends AbstractFacade  {

  override val wampApi: JawampaClient = buildWamp(TR.string.bitfinex_wamp.value)
  override val publicApi = ???
  override val privateApi = ???

  override def chartData(pair: CurrencyPair, start: Long, end: Long, period: Int): Seq[Chart] = Seq.empty

  override def wampSubscribe[WM <: WampMsg : Manifest](subs: WampSub[WM]): Unit = ???

  override def wampClose: Unit = ???

  override def ordersBook(pair: CurrencyPair, depth: Int): OrdersBook = ???

  override def tradeHistory(pair: CurrencyPair): Seq[TradeHistory] = ???

  override def balances: Map[String, String] = ???

  override def returnTicket: Map[String, Ticker] = ???

  override def ordersBook(depth: Int): Map[String, OrdersBook] = ???

  override def currencies(): Map[String, Currency] = ???

}
