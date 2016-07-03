package app.bitrader.api.poloniex

import java.net.URL

import app.bitrader.api.common.CurrencyPair._
import okhttp3.{HttpUrl, OkHttpClient, Request, Response}
import spray.json._

/**
  * Created by Alex Afanasev
  */
class PoloniexOkAPI {
  val ok = new OkHttpClient()
  import fommil.sjs.FamilyFormats._

  val baseUrl: HttpUrl = HttpUrl.get(new URL("https://poloniex.com/public?command=returnOrderBook"))

  def request(url: HttpUrl): Request = new Request.Builder().url(url).get().build()
  def execute(request1: Request): Response = ok.newCall(request1).execute()

  def returnTicker(): Map[String, Ticker] = {
    val response: Response = execute(request(baseUrl))
    response.body().string().parseJson.convertTo[Map[String, Ticker]]
  }

  def ordersBook(pair: CurrencyPair, depth : Int) : OrdersBook = {

    val r: Request = request(baseUrl.newBuilder()
      .addEncodedQueryParameter("depth", depth.toString)
      .addEncodedQueryParameter("currencyPair", pair.toString)
      .build())

    val response: Response = execute(r)
    response.body().string().parseJson.convertTo[OrdersBook]
  }

  def ordersBook(depth : Int) : Map[String, OrdersBook] = {

    val r: Request = request(baseUrl.newBuilder()
      .addEncodedQueryParameter("depth", depth.toString)
      .addEncodedQueryParameter("currencyPair", "all")
      .build())

    val response: Response = execute(r)
    response.body().string().parseJson.convertTo[Map[String, OrdersBook]]
  }

}
