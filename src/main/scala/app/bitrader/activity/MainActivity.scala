package app.bitrader.activity

import android.app.Activity
import android.content.res.{Configuration, Resources}
import android.graphics.{Color, PorterDuff, PorterDuffColorFilter}
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener
import android.support.design.widget._
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.widget.{DrawerLayout, NestedScrollView}
import android.support.v7.app.{ActionBarDrawerToggle, AppCompatActivity}
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.{CardView, SearchView, Toolbar}
import android.view._
import android.widget._
import app.bitrader._
import app.bitrader.activity.menu.{ProfileActivity, ReadQrActivity, WampActivity}
import app.bitrader.api.ApiProvider
import app.bitrader.api.bitfinex.Bitfinex
import app.bitrader.api.poloniex.{Chart, Poloniex}
import app.bitrader.helpers.Id
import app.bitrader.helpers.activity.ActivityOperations
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.{CandleData, CandleDataSet, CandleEntry}
import com.joanzapata.iconify.widget.IconTextView
import diode.ModelR
import io.github.aafa.drawer.{BasicDrawerLayout, DrawerActivity, DrawerMenuItem}
import io.github.aafa.helpers.{Styles, UiOperations, UiThreading}
import io.github.aafa.macroid.AdditionalTweaks
import macroid.FullDsl._
import macroid._

import scala.collection.JavaConverters._

/**
  * Created by aafa
  */

class MainActivity extends AppCompatActivity with Contexts[AppCompatActivity]
  with ActivityOperations with MenuItems with DrawerItems {

  private val appCircuit = AppCircuit
  private val chartSub = appCircuit.dataSubscribe(_.chartsData)(layout.updateChartData)
  private val contextZoom = appCircuit.serviceContext
  private val selectedApiSubscription = appCircuit
    .subscribe(appCircuit.zoom(_.selectedApi))(m => updateApi(m.value))

  lazy val layout = new MainActivityLayout(appCircuit, this.getLayoutInflater)

  override def onCreate(b: Bundle): Unit = {
    this.setTheme(contextZoom.zoom(_.theme).value)

    super.onCreate(b)
    setContentView(layout.ui.get)

    //todo setup charts wamp update

    //    APIContext.poloniexService(_.currencies()) map layout.updateData
    layout.updateChartData(appCircuit.serviceData.zoom(_.chartsData).value)
    appCircuit(UpdateCharts)

    setSupportActionBar(layout.toolbarView)
    setTitle(appCircuit.zoom(_.selectedApi).value.toString)
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)
  }


  override def onApplyThemeResource(theme: Resources#Theme, resid: Int, first: Boolean): Unit = {
    super.onApplyThemeResource(theme, resid, first)
    println(s"onApplyThemeResource $theme; $resid; $first")
  }

  override def onPause(): Unit = {
    super.onPause()
    chartSub.apply()
    selectedApiSubscription.apply()
  }


  def updateApi(value: ApiProvider): Unit = {
    startActivity[MainActivity]
    this.finish()
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
      this.changeTheme(R.style.MainTheme)
    }),
    DrawerMenuItem("TestActivity", action = () => {
      startActivity[TestActivity]
      this.changeTheme(R.style.GreenTheme)
    }),
    DrawerMenuItem("Account")
  )

  override def drawerLayout: Option[DrawerLayout] = Some(layout.mainView)

  override def toolbarView: Option[Toolbar] = Some(layout.toolbarView)
}

trait DrawerItems extends AppCompatActivity with Contexts[AppCompatActivity] with ActivityOperations with OnNavigationItemSelectedListener{
  import TypedResource._
  var actionBarDrawerToggle: Option[ActionBarDrawerToggle] = None

  def drawerLayout: Option[DrawerLayout]

  def toolbarView: Option[Toolbar]

  override def setContentView(view: View): Unit = {
    super.setContentView(view)
    addDrawerToggler
  }

  override def onNavigationItemSelected(item: MenuItem): Boolean ={

    item.getItemId match {
      case R.id.wamp =>
        startActivity[WampActivity]

      case R.id.readQrActivity =>
        startActivity[ReadQrActivity]
      case _ =>
    }

    true
  }

  def addDrawerToggler: Unit = {
    drawerLayout map { drawerLayout =>
      val drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, android.R.string.ok, android.R.string.cancel) {
        override def onDrawerClosed(drawerView: View): Unit = {
          super.onDrawerClosed(drawerView)
          invalidateOptionsMenu()
        }

        override def onDrawerOpened(drawerView: View): Unit = {
          super.onDrawerOpened(drawerView)
          invalidateOptionsMenu()
        }
      }
      actionBarDrawerToggle = Some(drawerToggle)
      drawerLayout.setDrawerListener(drawerToggle)

      val navigationView: NavigationView = drawerLayout.findView(TR.nav_view)
      navigationView.setNavigationItemSelectedListener(this)
    }

    toolbarView map { tb =>
      //      setSupportActionBar(tb)
      //      getSupportActionBar.setDisplayHomeAsUpEnabled(true)
      //      getSupportActionBar.setHomeButtonEnabled(true)
    }

  }

  override def onPostCreate(savedInstanceState: Bundle): Unit = {
    super.onPostCreate(savedInstanceState)
    actionBarDrawerToggle map (_.syncState)
  }

  override def onConfigurationChanged(newConfig: Configuration): Unit = {
    super.onConfigurationChanged(newConfig)
    actionBarDrawerToggle map (_.onConfigurationChanged(newConfig))
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if (actionBarDrawerToggle.isDefined && actionBarDrawerToggle.get.onOptionsItemSelected(item)) true
    else super.onOptionsItemSelected(item)
  }
}

trait MenuItems extends AppCompatActivity with Styles {

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    super.onOptionsItemSelected(item)
    println("item selected! " + item.getTitle)
    true
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    super.onCreateOptionsMenu(menu)
    getMenuInflater.inflate(R.menu.menu, menu)

    true
  }

}

class MainActivityLayout(
                          appCircuit: AppCircuit.type,
                          li: LayoutInflater
                        )
                        (implicit cw: ContextWrapper)
  extends MainStyles with ChartLayout with UiThreading {

  import TypedResource._

  private val zoomCurrencies = appCircuit.serviceData.zoom(_.currencies)

  var textSlot = slot[IconTextView]
  var btn = slot[Button]

  val longString: String = {
    def gen: Stream[String] = Stream.cons("I {fa-heart-o} to {fa-code} on {fa-android}", gen)
    gen.take(300) mkString " "
  }

  lazy val img: Drawable = TR.drawable.material_flat.get
  lazy val candleChartUi = w[CandleStickChart] <~ wire(candleStick) <~ candleStickSettings <~ vContentSizeMatchWidth(TR.dimen.chartHeight.get)

  def ui: Ui[View] = if (portrait)
    verticalLayout
  else
    candleChartUi <~ vMatchParent

  val mainView: DrawerLayout = li.inflate(TR.layout.activity_main)
  val toolbarView: Toolbar = mainView.findView(TR.flexible_example_toolbar)
  val graphSlot: LinearLayout = mainView.findView(TR.graphSlot)

  def verticalLayout: Ui[View] = {
    graphSlot.addView(candleChartUi.get)
    Ui(mainView)
  }

  // style


  def scrollFlags = modifyLpTweak[AppBarLayout.LayoutParams](lp => {
    lp.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
      | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)
  })

  //      w[FloatingActionButton] <~ drawable(AwesomeIcon(FontAwesomeIcons.fa_plus)) <~ fabTweak <~ SnackBuilding.snack("Test")

  def ctlTweak = Tweak[CollapsingToolbarLayout](ctl => {
    ctl.setExpandedTitleMarginBottom(TR.dimen.appbar_overlay.get)
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

trait MainStyles extends UiOperations with Styles with AdditionalTweaks

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
