package app.bitrader.api

import app.bitrader._
import app.bitrader.activity.ChartLayout
import app.bitrader.api.common.CurrencyPair
import app.bitrader.api.poloniex.Chart
import com.github.mikephil.charting.data.CandleData
import com.github.nscala_time.time.Imports._

/**
  * Created by Alex Afanasev
  */
class ChartSpec extends ApiSpec {

  class TestChartLayout extends ChartLayout

  it should "receive chart data" in {
    val charts: Seq[Chart] = poloniex.chartData(CurrencyPair.BTC_ETH, 5.hours.ago().unixtime, DateTime.now.unixtime, 300)
    assert(charts.nonEmpty)
    assert(charts.last.high > 0)
    println(s"charts.last.high ${charts.last.high}")


    val tcl = new TestChartLayout
    val candleData: CandleData = tcl.prepareChartData(charts)

    assert(candleData != null)
    assert(candleData.getDataSetCount > 0)
    assert(candleData.getDataSetByIndex(0).getEntryCount > 0)
    assert(candleData.getDataSetByIndex(0).getEntryForIndex(0).getOpen > 0)
  }
}
