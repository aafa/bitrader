package app.bitrader.api.poloniex

import java.util.Date

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}

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
                  @JsonProperty("date") var unixtime: Long,
                  @JsonProperty("high") var high: BigDecimal,
                  @JsonProperty("low") var low: BigDecimal,
                  @JsonProperty("open") var open: BigDecimal,
                  @JsonProperty("close") var close: BigDecimal,
                  @JsonProperty("volume") var volume: BigDecimal,
                  @JsonProperty("quoteVolume") var quoteVolume: BigDecimal,
                  @JsonProperty("weightedAverage") var weightedAverage: BigDecimal
                 )

@JsonCreator
case class TickerCollection(
                   @JsonProperty("last_price") var last_price: Option[java.lang.Float],
                   @JsonProperty("low") var low: Option[String],
                   @JsonProperty("high") var high: Option[Float],
                   @JsonProperty("mid") var mid: Float
                 )

trait Enum[A] {
  trait Value { self: A =>
    _values :+= this
  }
  private var _values: List[A] = List.empty[A]
  def values = _values
}

sealed trait CurrencyPair extends CurrencyPair.Value
object CurrencyPair extends Enum[CurrencyPair] {
  case object BTC_ETH extends CurrencyPair
  case object BTC_NXT extends CurrencyPair
  case object BTC_XMR extends CurrencyPair
}