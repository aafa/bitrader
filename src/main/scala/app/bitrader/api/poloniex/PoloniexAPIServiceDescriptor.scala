package app.bitrader.api.poloniex

import app.bitrader.api.APIDescriptor
import retrofit.http.{GET, Path, Query}

/**
  * Created by Alexey Afanasev on 21.04.16.
  */
trait PoloniexAPIServiceDescriptor {
  @GET("/public?command=returnTicker") def returnTicker(): Map[String, Ticker]

  @GET("/public?command=returnChartData")
  def chartData(@Query("currencyPair") pair: CurrencyPair, @Query("start") start: Long, @Query("end") end: Long, @Query("period") period: Int): Seq[Chart]

  @GET("/public?command=returnTradeHistory")
  def tradeHistory

  @GET("/public?command=returnOrderBook")
  def orderBook
}

