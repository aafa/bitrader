package app.bitrader.api

import android.os.Build.VERSION_CODES._
import app.bitrader.api.poloniex.Currency
import org.robolectric.annotation.Config
import org.assertj.android.api.Assertions._

/**
  * Created by Alex Afanasev
  */
class DirectoriesSpec extends ApiSpec {


  it should "work with currencies" in {
    val cc: Map[String, Currency] = poloniex.currencies().printTime
    val cc2: Map[String, Currency] = poloniex.currencies().printTime
    assert(cc.nonEmpty)

    cc map {
      case (coin, details) =>
        assert(coin.nonEmpty)
        assert(details.name.nonEmpty)
        assert(details.minConf > 0)
    }
  }

}
