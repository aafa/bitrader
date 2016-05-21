package app.bitrader.activity

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget._
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.widget.{DrawerLayout, NestedScrollView}
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.{CardView, SearchView, Toolbar}
import android.view._
import android.widget.LinearLayout
import app.bitrader.activity.menu.{ProfileActivity, ReadQrActivity, WampActivity}
import app.bitrader.api.poloniex.{Chart, Currency}
import app.bitrader.helpers.Id
import app.bitrader.helpers.activity.ActivityOperations
import app.bitrader._
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.{CandleData, CandleDataSet, CandleEntry}
import com.joanzapata.iconify.widget.IconTextView
import diode.ModelR
import io.github.aafa.drawer.{BasicDrawerLayout, DrawerActivity, DrawerMenuItem}
import io.github.aafa.helpers.{Styles, UiOperations}
import io.github.aafa.macroid.{AdditionalTweaks, ThemedBlocks}
import io.github.aafa.toolbar.ToolbarAboveLayout
import macroid.FullDsl._
import macroid._

import scala.collection.JavaConverters._

/**
  * Created by aafa
  */

class MainActivity extends DrawerActivity with ActivityOperations with MenuItems {

  private val appCircuit = AppCircuit
  private val zoomCurrencies = appCircuit.serviceData.zoom(_.currencies)
  private val chartSub = appCircuit.dataSubscribe(_.chartsData)(layout.updateChartData)


  override lazy val layout = new MainActivityLayout(menuItems, zoomCurrencies)

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    setContentView(layout.ui.get)

    //todo setup charts wamp update

    //    APIContext.poloniexService(_.currencies()) map layout.updateData
    layout.updateChartData(appCircuit.serviceData.zoom(_.chartsData).value)
    appCircuit(UpdateCharts)
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
    DrawerMenuItem("TestActivity", action = () => {
      startActivity[TestActivity]
    }),
    DrawerMenuItem("Account")
  )
}

trait MenuItems extends DrawerActivity with Styles {

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    super.onOptionsItemSelected(item)
    println("item selected! " + item.getTitle)
    true
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    super.onCreateOptionsMenu(menu)

    val search: MenuItem = menu.add("search")
    search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    search.setActionView(
      (w[SearchView] <~ Tweak[SearchView](sv => {
        sv.setQueryHint("coin pairs")
      }) <~ vMatchWidth).get
    )
    true
  }

}

class MainActivityLayout(override val menuItems: Seq[DrawerMenuItem],
                         zoomCurrencies: ModelR[RootModel, Map[String, Currency]]
                        )
                        (implicit cw: ContextWrapper, managerContext: FragmentManagerContext[Fragment, FragmentManager])
  extends BasicDrawerLayout(menuItems) with MainStyles with ChartLayout  {


  var textSlot = slot[IconTextView]

  val longString: String = {
    def gen: Stream[String] = Stream.cons("I {fa-heart-o} to {fa-code} on {fa-android}", gen)
    gen.take(300) mkString " "
  }

  lazy val img: Drawable = TR.drawable.material_flat.get

  def ui: Ui[View] = if (portrait)
    verticalLayout
  else
    w[CandleStickChart] <~ wire(candleStick) <~ candleStickSettings <~ vMatchParent

  def verticalLayout: Ui[DrawerLayout] = {
    import ThemedBlocks._

    drawer(
      l[CoordinatorLayout](
        appBar(ContextWrapper(new ContextThemeWrapper(cw.bestAvailable, R.style.ThemeOverlay_AppCompat_Dark))),

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


  def appBar(c : ContextWrapper): Ui[AppBarLayout] = {
    implicit val cw = c
    println(s"actual Context $c")

    l[AppBarLayout](
      l[CollapsingToolbarLayout](
        w[Toolbar] <~ wire(toolBar) <~ vContentSizeMatchWidth(TR.dimen.toolbar_height.get) <~ pin
      ) <~ vMatchParent <~ scrollFlags <~ ctlTweak
    ) <~ id(Id.appbar) <~ vContentSizeMatchWidth(180.dp)
  }


  def scrollFlags = modifyLpTweak[AppBarLayout.LayoutParams](lp => {
    lp.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
      | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)
  })

  //      w[FloatingActionButton] <~ drawable(AwesomeIcon(FontAwesomeIcons.fa_plus)) <~ fabTweak <~ SnackBuilding.snack("Test")

  def ctlTweak = Tweak[CollapsingToolbarLayout](ctl => {
    ctl.setContentScrimColor(TR.color.primary.get)
    //    ctl.setCollapsedTitleTextColor(Color.WHITE)
    //    ctl.setExpandedTitleColor(Color.WHITE)
    ctl.setExpandedTitleMarginBottom(45.dp)
  })

  def fitsAll: Transformer = {
    Transformer {
      case a => a <~ fits
    }
  }


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

trait MainStyles extends UiOperations with Styles with AdditionalTweaks {

}

trait ChartLayout {
  var candleStick = slot[CandleStickChart]

  def updateChartData(chartData: Seq[Chart]): Unit = if (chartData.nonEmpty) {
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
