package app.bitrader.api.poloniex

import retrofit.http._

/**
  * Created by Alex Afanasev
  */

trait PoloniexTradingAPIServiceDescriptor {

  @POST("/tradingApi")
  @FormUrlEncoded
  def balances(@Field("nonce") nonce: String, @Field("command") command: String): Map[String, String]

}