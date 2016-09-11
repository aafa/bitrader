package app.bitrader.api.apitest

import android.content.Context
import app.bitrader.api.common.CurrencyPair.CurrencyPair
import app.bitrader.api.common.WampMsg
import app.bitrader.api.network.WampSub
import app.bitrader.api.poloniex._
import app.bitrader.api.{AbstractFacade, ApiProvider}

/**
  * Created by Alex Afanasev
  */
case object ApiTest extends ApiProvider {
  override type ApiFacade = ApiTestFacade

  override def facade(implicit ctx: Context) = new ApiTestFacade
}


class ApiTestFacade(implicit ctx: Context) extends AbstractFacade {
  val ordersBook1: OrdersBook = OrdersBook(Seq(("1", BigDecimal(1))), Seq(("1", BigDecimal(1))), "0", 1)

  def returnTicket: Map[String, Ticker] = Map("testBtc" -> Ticker("123", "100", "150", "15"))

  def ordersBook(pair: CurrencyPair, depth: Int): OrdersBook = ordersBook1

  def ordersBook(depth: Int): Map[String, OrdersBook] = Map("test" -> ordersBook1)

  def tradeHistory(pair: CurrencyPair): Seq[TradeHistory] = Seq.empty

  def chartData(pair: CurrencyPair, start: Long, end: Long, period: Int): Seq[Chart] = Seq(Chart(
    123, BigDecimal(123), BigDecimal(123), BigDecimal(123),
    BigDecimal(123), BigDecimal(123), BigDecimal(123), BigDecimal(123)
  ))

  def currencies(): Map[String, Currency] = Map.empty

  def wampSubscribe[WM <: WampMsg : scala.reflect.Manifest](sub: WampSub[WM]): Unit = {}

  def wampClose: Unit = {}

  // private

  def balances: Map[String, String] = Map("testBtx" -> "1")

}
