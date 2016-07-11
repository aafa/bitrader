package app.bitrader.api.bitfinex

import app.bitrader.api.AbstractApi

/**
  * Created by Alexey Afanasev on 21.04.16.
  */
class BitfinexPublicAPI(url: String) extends AbstractApi(url) {
  import fommil.sjs.FamilyFormats._

  //  @GET("/pubticker/{symbol}")
  // todo path variables
  def pubticker(symbol: String): Ticker = {
    get[Ticker](Map(
      "symbol" -> symbol
    ))
  }

}
