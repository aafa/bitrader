package app.bitrader.activity

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.{Bundle, Parcel}
import android.support.design.widget._
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.widget.{DrawerLayout, NestedScrollView}
import android.support.v7.widget.{CardView, Toolbar}
import android.view.{Gravity, View}
import android.widget.LinearLayout
import app.bitrader.api.poloniex.{Chart, Currency, CurrencyPair, Poloniex}
import app.bitrader.helpers.Id
import app.bitrader.{APIContext, TR}
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.{CandleData, CandleDataSet, CandleEntry}
import com.joanzapata.iconify.widget.IconTextView
import io.github.aafa.drawer.{BasicDrawerLayout, DrawerActivity, DrawerMenuItem}
import io.github.aafa.helpers.{Styles, UiOperations, UiThreading}
import io.github.aafa.macroid.AdditionalTweaks
import io.github.aafa.toolbar.ToolbarAboveLayout
import macroid.FullDsl._
import macroid._

import collection.JavaConverters._
import com.github.nscala_time.time.Imports._

import scala.concurrent.ExecutionContext.Implicits.global
import app.bitrader._
import app.bitrader.activity.menu.{ProfileActivity, ReadQrActivity, WampActivity}
import app.bitrader.helpers.activity.ActivityOperations
import diode.ModelR

/**
  * Created by aafa
  */

class MainActivity extends DrawerActivity with ActivityOperations {

  private val appCircuit = AppCircuit
  private val selectedApi = Poloniex
  private val zoomCurrencies: ModelR[RootModel, Map[String, Currency]] = appCircuit.serviceData.zoom(_.currencies)
  private val chartSub: () => Unit = appCircuit.subscribe(
    appCircuit.serviceData.zoom(_.chartsData))(m => layout.updateChartData(m.value)
  )

  override lazy val layout = new MainActivityLayout(menuItems, zoomCurrencies)

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    setContentView(layout.ui.get)

    //todo setup charts wamp update

//    APIContext.poloniexService(_.currencies()) map layout.updateData
    appCircuit(UpdateCharts(selectedApi))
    layout.updateChartData(appCircuit.zoom(_.serviceContext(selectedApi).serviceData.chartsData).value)
  }



  override def onPause(): Unit = {
    super.onPause()
    chartSub.apply()
  }

  lazy val menuItems: Seq[DrawerMenuItem] = Seq(
    DrawerMenuItem("Wamp", action = () => {
      startActivity[WampActivity]
    }),
    DrawerMenuItem("ProfileActivity", action = () => {
      startActivity[ProfileActivity]
    }),
    DrawerMenuItem("ReadQrActivity", action = () => {
      startActivity[ReadQrActivity]
    }),
    DrawerMenuItem("Account")
  )
}

class MainActivityLayout(override val menuItems: Seq[DrawerMenuItem],
                         zoomCurrencies: ModelR[RootModel, Map[String, Currency]]
                        )
                        (implicit cw: ContextWrapper, managerContext: FragmentManagerContext[Fragment, FragmentManager])
  extends BasicDrawerLayout(menuItems) with MainStyles with ChartLayout {


  var textSlot = slot[IconTextView]

  val longString: String = {
    def gen: Stream[String] = Stream.cons("I {fa-heart-o} to {fa-code} on {fa-android}", gen)
    gen.take(300) mkString " "
  }

  def img: Drawable = TR.drawable.material_flat.get

  def ui: Ui[View] = if (portrait)
    verticalLayout
  else
    w[CandleStickChart] <~ wire(candleStick) <~ candleStickSettings <~ vMatchParent


  lazy val verticalLayout: Ui[DrawerLayout] = {
    drawer(
      l[CoordinatorLayout](
        l[AppBarLayout](
          l[CollapsingToolbarLayout](
            w[Toolbar] <~ wire(toolBar) <~ vContentSizeMatchWidth(TR.dimen.toolbar_height.get) <~ pin
          ) <~ vMatchParent <~ scrollFlags <~ ctlTweak
        ) <~ id(Id.appbar) <~ vContentSizeMatchWidth(180.dp),

        l[NestedScrollView](
          l[LinearLayout](
            l[CardView](
              w[CandleStickChart] <~ wire(candleStick) <~ candleStickSettings
            ) <~ vContentSizeMatchWidth(200.dp) <~ cardTweak <~ id(Id.card),

            l[CardView](
              l[LinearLayout](
                w[IconTextView] <~ wire(textSlot) <~ text(zoomCurrencies.value.toString())
              )
            ) <~ vMatchWidth <~ cardTweak
          ) <~ vertical
        ) <~ vMatchParent <~ nestedScroll(40.dp)
      ) <~ vMatchParent
    )
  }


  // style

  def fitsAll: Transformer = {
    Transformer {
      case a => a <~ fits
    }
  }

  def scrollFlags = modifyLpTweak[AppBarLayout.LayoutParams](lp => {
    lp.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
      | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)
  })

  //      w[FloatingActionButton] <~ drawable(AwesomeIcon(FontAwesomeIcons.fa_plus)) <~ fabTweak <~ SnackBuilding.snack("Test")

  def ctlTweak = Tweak[CollapsingToolbarLayout](ctl => {
    ctl.setContentScrimColor(TR.color.primary.get)
    ctl.setCollapsedTitleTextColor(Color.WHITE)
    ctl.setExpandedTitleColor(Color.WHITE)
    ctl.setExpandedTitleMarginBottom(45.dp)
  })

  def fabTweak = modifyLpTweak[CoordinatorLayout.LayoutParams](params => {
    params.anchorGravity = Gravity.TOP | Gravity.RIGHT | Gravity.END
    params.setAnchorId(Id.card)
    params.setMarginEnd(20.dp)
  })


  def cardTweak: Tweak[CardView] = Tweak[CardView](c => {
    val p = 10.dp
    c.setRadius(5.dp)
    c.setCardElevation(2.dp)
    c.setContentPadding(p, p, p, p)
  }) + margin(all = 10.dp)

}

trait MainStyles extends ToolbarAboveLayout with UiOperations with Styles with AdditionalTweaks {

}

trait ChartLayout {
  var candleStick = slot[CandleStickChart]

  def updateChartData(chartData: Seq[Chart]): Unit = if (chartData.nonEmpty){
    val data: CandleData = prepareChartData(chartData)
    Ui.run(updateChartUi(data))
  }

  def prepareChartData(chartData: Seq[Chart]): CandleData = {
    val xs: Seq[String] = chartData map (_.unixtime.utimeFormatted)
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
