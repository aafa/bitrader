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
                   @JsonProperty("date") var date: Long,
                   @JsonProperty("high") var high: String,
                   @JsonProperty("low") var low: String,
                   @JsonProperty("open") var open: String,
                   @JsonProperty("close") var close: String,
                   @JsonProperty("volume") var volume: String,
                   @JsonProperty("quoteVolume") var quoteVolume: String,
                   @JsonProperty("weightedAverage") var weightedAverage: String
                 )

@JsonCreator
case class TickerCollection(
                   @JsonProperty("last_price") var last_price: Option[java.lang.Float],
                   @JsonProperty("low") var low: Option[String],
                   @JsonProperty("high") var high: Option[Float],
                   @JsonProperty("mid") var mid: Float
                 )