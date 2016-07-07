package app.bitrader.api.poloniex

import java.net.URL
import java.util.Date

import app.bitrader.api.common.CurrencyPair._
import okhttp3.HttpUrl.Builder
import okhttp3.{HttpUrl, OkHttpClient, Request, Response}
import spray.json._

/**
  * Created by Alex Afanasev
  */
class PoloniexOkAPI {
  val ok = new OkHttpClient()
  import fommil.sjs.FamilyFormats._

  val baseUrl: HttpUrl = HttpUrl.get(new URL("https://poloniex.com/public"))

  def request(url: HttpUrl): Request = new Request.Builder().url(url).get().build()
  def execute(request1: Request): Response = ok.newCall(request1).execute()

  def get[Result : JsonReader](params: Map[String, String]): Result = {
    def nonce: String = new Date().getTime.toString
    val reqBuilder: Builder = baseUrl.newBuilder()

    for ((k,v) <- params){
      reqBuilder.addEncodedQueryParameter(k, v)
    }

    reqBuilder.addEncodedQueryParameter("nonce", nonce)

    val r: Request = request(reqBuilder.build())
    val response: Response = execute(r)
    response.body().string().parseJson.convertTo[Result]
  }

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

}
