package app.bitrader.api

import android.os.Build.VERSION_CODES._
import app.bitrader.api.poloniex.Chart
import org.robolectric.annotation.Config

/**
  * Created by Alex Afanasev
  */
@Config(sdk = Array(LOLLIPOP))
class ChartSpec extends ApiSpec {

  it should "receive chart data" in {
    val charts: Seq[Chart] = poloniexApi.chartData("BTC_ETH", 1405699200, 1909699200, 14400)
    assert(charts.nonEmpty)
    assert(charts.head.high > 0)
  }
}
