package app.bitrader.api.poloniex

import app.bitrader.api.AbstractApi
import app.bitrader.api.network.AuthInterceptor
import fommil.sjs.FamilyFormats._
import okhttp3.OkHttpClient
/**
  * Created by Alex Afanasev
  */
class PoloniexOkTradingAPI(url: String) extends AbstractApi(url) {

  override lazy val httpClient: OkHttpClient = new OkHttpClient.Builder()
    .addInterceptor(new AuthInterceptor())
    .build()

  def balances(): Map[String, String] = post[Map[String, String]](Map(
    "command" -> "returnBalances"
  ))

  def returnOpenOrders(currencyPair: String): Map[String, Seq[OrderDetails]] =
    post[Map[String, Seq[OrderDetails]]](Map(
      "command" -> "returnOpenOrders",
      "currencyPair" -> currencyPair
    ))

  def returnTradeHistory(pair: String): Seq[Map[String, Seq[TradeHistory]]] =
    post[Seq[Map[String, Seq[TradeHistory]]]](Map(
      "command" -> "returnTradeHistory",
      "currencyPair" -> pair
    ))

  def buy(currencyPair: String, rate: Double, amount: Double): ActualOrder = post[ActualOrder](Map(
    "command" -> "buy",
    "currencyPair" -> currencyPair,
    "rate" -> rate.toString,
    "amount" -> amount.toString
  ))

  def sell(currencyPair: String, rate: Double, amount: Double): ActualOrder = post[ActualOrder](Map(
    "command" -> "sell",
    "currencyPair" -> currencyPair,
    "rate" -> rate.toString,
    "amount" -> amount.toString
  ))

  def cancelOrder(number: String): Map[String, Int] =
    post[Map[String, Int]](Map(
      "command" -> "cancelOrder",
      "orderNumber" -> number
    ))

  def returnCompleteBalances(): Map[String, CompleteBalance] =
    post[Map[String, CompleteBalance]](Map(
      "command" -> "returnCompleteBalances",
      "account" -> "all"
    ))

  def returnDepositAddresses(): Map[String, String] =
    post[Map[String, String]](Map(
      "command" -> "returnDepositAddresses"
    ))


}
