package app.bitrader.api

import android.os.Build.VERSION_CODES._
import app.bitrader._
import app.bitrader.activity.ChartLayout
import app.bitrader.api.poloniex.{Chart, CurrencyPair}
import com.github.mikephil.charting.data.CandleData
import com.github.nscala_time.time.Imports._
import org.robolectric.annotation.Config

/**
  * Created by Alex Afanasev
  */
@Config(sdk = Array(LOLLIPOP))
class AuthApiSpec extends ApiSpec {

  it should "do auth" in {
    val balances: Map[String, String] = poloniexPrivateApi.balances()
    assert(balances.nonEmpty)
    assert(balances.head._1 != "error")

    println(s"balances $balances")

  }
}
