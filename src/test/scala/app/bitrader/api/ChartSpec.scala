package app.bitrader.api

import android.os.Build.VERSION_CODES._
import app.bitrader.activity.MainActivity
import app.bitrader.api.poloniex.Chart
import com.github.mikephil.charting.data.CandleData
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.res.FsFile
import org.robolectric.res.FsFile.Filter

/**
  * Created by Alex Afanasev
  */
@Config(sdk = Array(LOLLIPOP))
class ChartSpec extends ApiSpec {

  it should "have aars in place" in {
    val support: String = "com.android.support-appcompat-v7-23.2.1"

    assert(aarsDir.isDirectory)
    assert(aarsDir.listFiles().nonEmpty)
    assert(aarsDir.listFileNames().contains(support))
    assert(aarsDir.listFiles(new Filter {
      override def accept(fsFile: FsFile): Boolean = fsFile.getName.contains(support)
    }).head.getBytes.nonEmpty)
  }

  it should "work with activities" in {
    val activity: MainActivity = Robolectric.buildActivity(classOf[MainActivity]).create().get()
    assert(activity != null)
  }

  it should "receive chart data" in {
    val charts: Seq[Chart] = poloniexApi.chartData("BTC_ETH", 1405699200, 1909699200, 14400)
    assert(charts.nonEmpty)
    assert(charts.last.high > 0)
    println(s"charts.last.high ${charts.last.high}")


//    val activity: MainActivity = Robolectric.buildActivity(classOf[MainActivity]).create().get()
//    val candleData: CandleData = activity.updateChart(charts)
//
//    assert(candleData != null)
//    println(candleData)
  }
}
