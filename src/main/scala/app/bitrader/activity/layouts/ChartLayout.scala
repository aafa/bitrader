package app.bitrader.activity.layouts

import app.bitrader._
import app.bitrader.api.poloniex.Chart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.{CandleData, CandleDataSet, CandleEntry}
import diode.ModelR
import macroid.FullDsl._
import macroid._

import scala.collection.JavaConverters._
import scala.language.postfixOps

/**
  * Created by Alex Afanasev
  */
trait ChartLayout {
  var candleStick = slot[CandleStickChart]

  def updateChartData(chartData: Seq[Chart]): Unit = if (chartData.nonEmpty) {
    val data: CandleData = prepareChartData(chartData)
    Ui.run(updateChartUi(data))
  }

  def prepareChartData(chartData: Seq[Chart]): CandleData = {
    val xs: Seq[String] = chartData map (_.date.utimeFormatted)
    val ys: Seq[CandleEntry] = chartData map (chart => new CandleEntry(chartData.indexOf(chart),
      chart.high.floatValue(), chart.low.floatValue(), chart.open.floatValue(), chart.close.floatValue()))
    val set = new CandleDataSet(ys.asJava, "Data")

    val data: CandleData = new CandleData(xs.asJava, set)
    data.setDrawValues(false)
    data
  }

  // for wamp updates
  def updateChartUi(data: CandleData): Ui[_] = {
    candleStick <~ Tweak[CandleStickChart](cs => {
      cs.setData(data)
      cs.invalidate()
    })
  }

  def candlestickData(m: ModelR[RootModel, Seq[Chart]]) = Tweak[CandleStickChart](cs => if (m.value.nonEmpty) {
    cs.setData(prepareChartData(m.value))
    cs.invalidate()
  })

  val candleStickSettings = Tweak[CandleStickChart](
    _.setDescription("Bitrader data")

  )
}
