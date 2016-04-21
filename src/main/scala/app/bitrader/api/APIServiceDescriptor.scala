package app.bitrader.api

import app.bitrader.model.Ticker
import retrofit.http.{GET, Path}

/**
  * Created by Alexey Afanasev on 21.04.16.
  */
trait APIServiceDescriptor {
  @GET("/pubticker/{symbol}") def pubticker(@Path("symbol") symbol: String): Ticker

}
