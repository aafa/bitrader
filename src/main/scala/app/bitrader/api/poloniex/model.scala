package app.bitrader.api.poloniex

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
case class TickerCollection(
                   @JsonProperty("last_price") var last_price: Option[java.lang.Float],
                   @JsonProperty("low") var low: Option[String],
                   @JsonProperty("high") var high: Option[Float],
                   @JsonProperty("mid") var mid: Float
                 )