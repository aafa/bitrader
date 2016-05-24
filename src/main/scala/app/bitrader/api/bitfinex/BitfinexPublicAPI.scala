package app.bitrader.api.bitfinex

import app.bitrader.api.APIDescriptor
import retrofit.http.{GET, Path}

/**
  * Created by Alexey Afanasev on 21.04.16.
  */
trait BitfinexPublicAPI {
  @GET("/pubticker/{symbol}") def pubticker(@Path("symbol") symbol: String): Ticker

}
