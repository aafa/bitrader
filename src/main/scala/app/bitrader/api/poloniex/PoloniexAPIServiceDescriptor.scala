package app.bitrader.api.poloniex

import app.bitrader.api.APIDescriptor
import retrofit.http._

/**
  * Created by Alexey Afanasev on 21.04.16.
  */
trait PoloniexAPIServiceDescriptor {

  @Header("Cache-Control: public, max-age=640000, s-maxage=640000 , max-stale=2419200")
  @GET("/public?command=returnTicker")
  def returnTicker(): Map[String, Ticker]

  @GET("/public?command=returnChartData")
  def chartData(@Query("currencyPair") pair: CurrencyPair,
                @Query("start") start: Long, @Query("end") end: Long, @Query("period") period: Int): Seq[Chart]

  @GET("/public?command=returnTradeHistory")
  def tradeHistory(@Query("currencyPair") pair: CurrencyPair,
                   @Query("start") start: Long, @Query("end") end: Long) : Seq[TradeHistory]


  /*  get recent 200 items
    */
  @GET("/public?command=returnTradeHistory")
  def tradeHistory(@Query("currencyPair") pair: CurrencyPair) : Seq[TradeHistory]

  @GET("/public?command=returnOrderBook")
  def ordersBook(@Query("currencyPair") pair: CurrencyPair, @Query("depth") depth : Int) : OrdersBook

  @GET("/public?command=returnOrderBook&currencyPair=all")
  def ordersBook(@Query("depth") depth : Int) : Map[String, OrdersBook]

  @GET("/public?command=returnCurrencies")
  @Header("Cache-Control: max-stale=3600")
  def currencies() : Map[String, Currency]
}


