package app.bitrader.api.poloniex

import app.bitrader.api.AbstractApi
import app.bitrader.api.common.CurrencyPair._
/**
  * Created by Alex Afanasev
  */
class PoloniexOkAPI(url: String) extends AbstractApi(url){

  def returnTicker(): Map[String, Ticker] = get[Map[String, Ticker]](Map(
    "command" -> "returnTicker"
  ))

  def ordersBook(pair: CurrencyPair, depth : Int) : OrdersBook = get[OrdersBook](Map(
    "command" -> "returnOrderBook",
    "depth" -> depth.toString,
    "currencyPair" -> pair.toString
  ))

  def ordersBook(depth : Int) : Map[String, OrdersBook] = get[Map[String, OrdersBook]](Map(
    "command" -> "returnOrderBook",
    "depth" -> depth.toString,
    "currencyPair" -> "all"
  ))

  def chartData(pair: CurrencyPair, start: Long, end: Long, period: Int): Seq[Chart] = get[Seq[Chart]](Map(
    "command" -> "returnChartData",
    "start" -> start.toString,
    "end" -> end.toString,
    "period" -> period.toString,
    "currencyPair" -> pair.toString
  ))

  def currencies() : Map[String, Currency] = get[Map[String, Currency]](Map(
    "command" -> "returnCurrencies"
  ))

  def tradeHistory(pair: CurrencyPair) : Seq[TradeHistory] = get[Seq[TradeHistory]](Map(
    "command" -> "returnTradeHistory",
    "currencyPair" -> pair.toString
  ))

}
