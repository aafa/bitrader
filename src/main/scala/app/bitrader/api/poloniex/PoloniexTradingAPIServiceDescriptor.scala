package app.bitrader.api.poloniex

import retrofit.http.GET

/**
  * Created by Alex Afanasev
  */

trait PoloniexTradingAPIServiceDescriptor {

  @GET("/tradingApi?command=returnBalances")
  def balances() : Map[String, String]

}