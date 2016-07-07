package app.bitrader.api.poloniex

import java.util.Date

import app.ObjectEnum
import app.bitrader._
import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import org.joda.time.DateTime

import scala.collection.SortedMap


/**
  * Created by Alexey Afanasev on 22.04.16.
  */
@JsonCreator
case class Ticker(
                   @JsonProperty("last") var last: String,
                   @JsonProperty("lowestAsk") var lowestAsk: String,
                   @JsonProperty("highestBid") var highestBid: String,
                   @JsonProperty("percentChange") var percentChange: String
                 )

@JsonCreator
case class Chart(
                  @JsonProperty("date") var date: Long,
                  @JsonProperty("high") var high: BigDecimal,
                  @JsonProperty("low") var low: BigDecimal,
                  @JsonProperty("open") var open: BigDecimal,
                  @JsonProperty("close") var close: BigDecimal,
                  @JsonProperty("volume") var volume: BigDecimal,
                  @JsonProperty("quoteVolume") var quoteVolume: BigDecimal,
                  @JsonProperty("weightedAverage") var weightedAverage: BigDecimal
                 )

@JsonCreator
case class OrdersBook(
                   @JsonProperty("asks") var asks: Seq[OrderPair],
                   @JsonProperty("bids") var bids: Seq[OrderPair],
                   @JsonProperty("isFrozen") var isFrozen: String,
                   @JsonProperty("seq") var seq: Long
                 ){

  def asksMap: OrdersMap = SortedMap(asks:_*)
  def bidsMap: OrdersMap = SortedMap(bids:_*)
}

@JsonCreator
case class Currency(
                   @JsonProperty("name") var name: String,
                   @JsonProperty("txFee") var txFee: BigDecimal,
                   @JsonProperty("minConf") var minConf: Int,
                   @JsonProperty("depositAddress") var depositAddress: Option[String],
                   @JsonProperty("disabled") var disabled: Byte,
                   @JsonProperty("delisted") var delisted: Byte,
                   @JsonProperty("frozen") var frozen: Byte
                 )

@JsonCreator
case class OrderDetails(
                         @JsonProperty("orderNumber") orderNumber : Long,
                         @JsonProperty("type") tpe: String,
                         @JsonProperty("rate") rate: BigDecimal,
                         @JsonProperty("amount") amount: Double,
                         @JsonProperty("total") total: Double
                       )


@JsonCreator
case class TradeHistory(
                         @JsonProperty("globalTradeID") globalTradeID: Option[Long],
                         @JsonProperty("tradeID") tradeID: String,
                         @JsonProperty("date") date: Date,
                         @JsonProperty("rate") rate: BigDecimal,
                         @JsonProperty("amount") amount: Double,
                         @JsonProperty("total") total: Option[Double],
                         @JsonProperty("fee") fee: Option[BigDecimal],
                         @JsonProperty("orderNumber") orderNumber: Option[String],
                         @JsonProperty("type") `type`: String,
                         @JsonProperty("category") category: Option[String]
                      )

@JsonCreator
case class ActualOrder(
                        @JsonProperty("orderNumber") orderNumber: String,
                        @JsonProperty("resultingTrades") resultingTrades: Seq[TradeHistory]
                      )

@JsonCreator
case class CompleteBalance(
                        @JsonProperty("available") available: BigDecimal,
                        @JsonProperty("onOrders") onOrders: BigDecimal,
                        @JsonProperty("btcValue") btcValue: BigDecimal
                      )