package app.bitrader.api.bitfinex

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}

/**
  * Created by Alexey Afanasev on 21.04.16.
  */
@JsonCreator
case class Ticker(
                   @JsonProperty("last_price") var last_price: Option[Float],
                   @JsonProperty("low") var low: Option[String],
                   @JsonProperty("high") var high: Option[Float],
                   @JsonProperty("mid") var mid: Float
                 )