package app.bitrader.api.poloniex

import app.bitrader.api.APIDescriptor
import retrofit.http.GET

/**
  * Created by Alexey Afanasev on 21.04.16.
  */
trait PoloniexAPIServiceDescriptor {
  @GET("/public?command=returnTicker") def returnTicker(): Map[String, Ticker]

}
