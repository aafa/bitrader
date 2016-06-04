package app.bitrader.api.poloniex

import app.bitrader.api.APIDescriptor
import retrofit.http._

/**
  * Created by Alex Afanasev
  */

trait PoloniexTradingAPI {

  @POST("/tradingApi")
  @FormUrlEncoded
  def post(@FieldMap map: java.util.Map[String, String]): Map[String, String]

  @POST("/tradingApi")
  @FormUrlEncoded
  def returnOpenOrders(@FieldMap map: java.util.Map[String, String]): Map[String, Seq[OrderDetails]]

  @POST("/tradingApi")
  @FormUrlEncoded
  def returnTradeHistory(@FieldMap map: java.util.Map[String, String]): Seq[Map[String, Seq[TradeHistory]]]

  @POST("/tradingApi")
  @FormUrlEncoded
  def placeOrder(@FieldMap map: java.util.Map[String, String]): ActualOrder

  @POST("/tradingApi")
  @FormUrlEncoded
  def cancelOrder(@FieldMap map: java.util.Map[String, String]): Map[String, Int]


}