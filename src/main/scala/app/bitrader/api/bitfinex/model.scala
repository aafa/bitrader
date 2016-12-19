package app.bitrader.api.bitfinex

import json.accessor

/**
  * Created by Alexey Afanasev on 21.04.16.
  */
@accessor
case class Ticker(
                    var last_price: Option[Float],
                    var low: Option[String],
                    var high: Option[Float],
                    var mid: Float
                 )