package app.bitrader.api

import android.os.Build.VERSION_CODES._
import app.bitrader.api.poloniex.Currency
import org.robolectric.annotation.Config

/**
  * Created by Alex Afanasev
  */
@Config(sdk = Array(LOLLIPOP))
class DirectoriesSpec extends ApiSpec {


  it should "work with currencies" in {
    val cc: Map[String, Currency] = poloniexApi.currencies().printTime
    val cc2: Map[String, Currency] = poloniexApi.currencies().printTime
    assert(cc.nonEmpty)

    cc map {
      case (coin, details) =>
        assert(coin.nonEmpty)
        assert(details.name.nonEmpty)
        assert(details.minConf > 0)
    }
  }

}
