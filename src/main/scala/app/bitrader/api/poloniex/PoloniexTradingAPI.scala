package app.bitrader.api.poloniex

import app.bitrader.api.APIDescriptor
import retrofit.http._

/**
  * Created by Alex Afanasev
  */

trait PoloniexTradingAPI {

  @POST("/tradingApi")
  @FormUrlEncoded
  def get(@Field("nonce") nonce: String, @Field("command") command: String): Map[String, String]

}