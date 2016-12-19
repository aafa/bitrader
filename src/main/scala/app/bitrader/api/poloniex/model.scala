package app.bitrader.api.poloniex

import app.bitrader._
import json.accessor

import scala.collection.SortedMap


/**
  * Created by Alexey Afanasev on 22.04.16.
  */
@accessor
case class Ticker(
                   var last: String,
                   var lowestAsk: String,
                    var highestBid: String,
                    var percentChange: String
                 )


@accessor
case class Chart(
                   var date: Long,
                   var high: BigDecimal,
                   var low: BigDecimal,
                   var open: BigDecimal,
                   var close: BigDecimal,
                   var volume: BigDecimal,
                   var quoteVolume: BigDecimal,
                   var weightedAverage: BigDecimal
                 )


@accessor
case class OrdersBook(
                    var asks: Seq[OrderPair],
                    var bids: Seq[OrderPair],
                    var isFrozen: String,
                    var seq: Long
                 ){

  def asksMap: OrdersMap = SortedMap(asks:_*)
  def bidsMap: OrdersMap = SortedMap(bids:_*)
}


@accessor
case class Currency(
                    var name: String,
                    var txFee: BigDecimal,
                    var minConf: Int,
                    var depositAddress: Option[String],
                    var disabled: Int,
                    var delisted: Int,
                    var frozen: Int
                 )


@accessor
case class OrderDetails(
                          orderNumber : Long,
                          tpe: String,
                          rate: BigDecimal,
                          amount: Double,
                          total: Double
                       )



@accessor
case class TradeHistory(
                          globalTradeID: Option[Long],
                          tradeID: String,
                          date: String,  // todo parse Date
                          rate: BigDecimal,
                          amount: Double,
                          total: Option[Double],
                          fee: Option[BigDecimal],
                          orderNumber: Option[String],
                          `type`: String,
                          category: Option[String]
                      )


@accessor
case class ActualOrder(
                         orderNumber: String,
                         resultingTrades: Seq[TradeHistory]
                      )


@accessor
case class CompleteBalance(
                         available: String,  // todo BigDecimal
                         onOrders: String,
                         btcValue: String
                      )