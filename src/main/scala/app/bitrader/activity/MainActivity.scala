package app.bitrader.activity

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget._
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.{CardView, Toolbar}
import android.view._
import android.widget._
import app.bitrader._
import app.bitrader.activity.menu.{ReadQrActivity, WampActivity}
import app.bitrader.api.ApiProvider
import app.bitrader.api.poloniex.Chart
import app.bitrader.helpers.Id
import app.bitrader.helpers.activity.ActivityOperations
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.{CandleData, CandleDataSet, CandleEntry}
import com.joanzapata.iconify.widget.IconTextView
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.materialdrawer.AccountHeader.OnAccountHeaderListener
import com.mikepenz.materialdrawer.Drawer.OnDrawerItemClickListener
import com.mikepenz.materialdrawer.model.interfaces.{IDrawerItem, IProfile}
import com.mikepenz.materialdrawer.model.{PrimaryDrawerItem, ProfileDrawerItem, ProfileSettingDrawerItem}
import com.mikepenz.materialdrawer.{AccountHeader, AccountHeaderBuilder, Drawer, DrawerBuilder}
import diode.ModelR
import io.github.aafa.helpers.{Styles, UiOperations, UiThreading}
import io.github.aafa.macroid.AdditionalTweaks
import macroid.FullDsl._
import macroid._
import TypedResource._
import android.support.v4.view.LayoutInflaterCompat
import android.widget.AdapterView.OnItemClickListener
import app.bitrader.api.common.CurrencyPair
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.miguelcatalan.materialsearchview.MaterialSearchView.OnQueryTextListener
import com.mikepenz.iconics.context.IconicsLayoutInflater
import com.orhanobut.logger.Logger

import scala.collection.JavaConverters._
import scala.language.postfixOps
import scala.util.Random

/**
  * Created by aafa
  */

class MainActivity extends AppCompatActivity with Contexts[AppCompatActivity]
  with ActivityOperations with MenuItems with DrawerSetup {

  val appCircuit = AppCircuit
  private val chartSub = appCircuit.dataSubscribe(_.chartsData)(layout.updateChartData)
  private val contextZoom = appCircuit.serviceContext
  private val selectedApiSubscription = appCircuit
    .subscribe(appCircuit.zoom(_.selectedApi))(m => updateApi(m.value))

  lazy val layout = new MainActivityLayout(appCircuit, this.getLayoutInflater)

  override def onCreate(b: Bundle): Unit = {
    this.setTheme(contextZoom.zoom(_.theme).value)
    LayoutInflaterCompat.setFactory(getLayoutInflater, new IconicsLayoutInflater(getDelegate))

    super.onCreate(b)
    setContentView(layout.ui.get)

    Logger.d("main!")

    //todo setup charts wamp update

    //    APIContext.poloniexService(_.currencies()) map layout.updateData
    layout.updateChartData(appCircuit.serviceData.zoom(_.chartsData).value)
    appCircuit(UpdateCharts(CurrencyPair.BTC_ETH))

    setSupportActionBar(layout.toolbarView)
    setTitle(appCircuit.zoom(_.selectedApi).value.toString)

    drawerSetup(this)
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
}

trait DrawerSetup {
  this: MainActivity =>

  def profileWrapper(k: ApiProvider): ProfileDrawerItem = {
    new ProfileDrawerItem().withName(k.toString).withIdentifier(Random.nextLong())  // inject random id to have them distinct
  }

  lazy val providers: Seq[ApiProvider] = appCircuit.zoom(_.serviceContext).value.keys.toSeq
  lazy val profileItems = providers zip (providers map profileWrapper) toMap
  lazy val apiKey: Map[ProfileDrawerItem, ApiProvider] = profileItems.map(_.swap)

  lazy val menuItems: Seq[IProfile[_]] = profileItems.values.toSeq :+
    new ProfileSettingDrawerItem().withName("Add profile").withIcon(GoogleMaterial.Icon.gmd_add)


  def drawerSetup(mainActivity: MainActivity) = {
    val accountHeader: AccountHeader = new AccountHeaderBuilder()
      .withActivity(mainActivity)
      .addProfiles(menuItems: _*)
      .withOnAccountHeaderListener(new OnAccountHeaderListener {
        override def onProfileChanged(view: View, item: IProfile[_], b: Boolean): Boolean = {
          item match {
            case p: ProfileDrawerItem => appCircuit(SelectApi(apiKey(p)))
            case s: ProfileSettingDrawerItem => // todo settings
          }

          true
        }
      })
      .withHeaderBackground(R.drawable.material_flat)
      .build()

    accountHeader.setActiveProfile(profileItems(appCircuit.zoom(_.selectedApi).value))

    type IDrawer = IDrawerItem[_, _ <: ViewHolder]
    def itemWrapper(s: String) = new PrimaryDrawerItem().withName(s)
      .withSelectable(false).withIdentifier(Random.nextLong())

    lazy val actions: Map[IDrawer, () => Unit] = Map(
      itemWrapper("Wamp") -> { () => startActivity[WampActivity] },
      itemWrapper("Read qr") -> { () => startActivity[ReadQrActivity] }
    )

    val drawer: Drawer = new DrawerBuilder().withActivity(mainActivity)
      .withToolbar(mainActivity.layout.toolbarView)
      .withAccountHeader(accountHeader)
      .addDrawerItems(actions.keys.toSeq: _*)
      .withOnDrawerItemClickListener(new OnDrawerItemClickListener {
        override def onItemClick(view: View, i: Int, item: IDrawer): Boolean = {
          actions(item).apply()
          true
        }
      })
      .withCloseOnClick(true)
      .build()

    drawer.setSelection(-1)
  }
}

trait MenuItems extends AppCompatActivity {
  self : MainActivity =>

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    super.onOptionsItemSelected(item)


    true
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    super.onCreateOptionsMenu(menu)
    getMenuInflater.inflate(R.menu.menu, menu)

    val searchView: MaterialSearchView = layout.mainView.findView(TR.search_view)
    searchView.setVisibility(View.VISIBLE)
    searchView.setMenuItem(menu.findItem(R.id.action_search))

    searchView.setSuggestions(CurrencyPair.values.toArray map (_.toString))

    searchView.setOnQueryTextListener(new OnQueryTextListener {
      override def onQueryTextSubmit(s: String): Boolean = {
        val hasValue: Boolean = CurrencyPair.values.exists(_.toString == s)
        if (hasValue) {
          appCircuit(UpdateCharts(CurrencyPair.withName(s)))
          searchView.closeSearch()
        }
        hasValue
      }

      override def onQueryTextChange(s: String): Boolean = {
        true
      }
    })

    searchView.setSubmitOnClick(true)

    true
  }

}

class MainActivityLayout(
                          appCircuit: AppCircuit.type,
                          li: LayoutInflater
                        )
                        (implicit cw: ContextWrapper)
  extends MainStyles with ChartLayout with UiThreading {


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

  val mainView: CoordinatorLayout = li.inflate(TR.layout.activity_flexible_space)
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
