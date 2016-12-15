package app.bitrader.activity

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget._
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.{CardView, Toolbar}
import android.view._
import android.widget.{FrameLayout, _}
import app.bitrader._
import app.bitrader.activity.menu.{PairsListFragment, ReadQrActivity, WampActivity}
import app.bitrader.api.ApiProvider
import app.bitrader.api.poloniex.Chart
import app.bitrader.helpers._
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
import macroid.FullDsl._
import macroid._
import TypedResource._
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.view.LayoutInflaterCompat
import android.widget.AdapterView.OnItemClickListener
import app.bitrader.activity.layouts.{BasicLayout, ChartLayout, DrawerLayout}
import app.bitrader.api.common.CurrencyPair
import com.fortysevendeg.macroid.extras.DrawerLayoutTweaks.dlCloseDrawer
import com.fortysevendeg.macroid.extras.ToolbarTweaks.tbTitle
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
  with ActivityOperations with MenuItems with Circuitable{

  private val chartSub = appCircuit.dataSubscribe(_.chartsData)(layout.updateChartData)
  private val contextZoom = appCircuit.serviceContext
  private val selectedApiSubscription = appCircuit
    .subscribe(appCircuit.zoom(_.selectedApi))(m => updateApi(m.value))

  lazy val layout = new MainActivityLayoutInflated(this.getLayoutInflater)
  private lazy val drawer = new DrawerLayout(appCircuit)

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

    drawer.drawerSetup(this)
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



trait MenuItems extends AppCompatActivity {
  self : MainActivity =>

  lazy val searchView: MaterialSearchView = layout.search_view

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    super.onOptionsItemSelected(item)


    true
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    super.onCreateOptionsMenu(menu)
    getMenuInflater.inflate(R.menu.menu, menu)

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

class MainActivityLayoutInflated(li: LayoutInflater)
                                (implicit cw: ContextWrapper,
                                 managerContext: FragmentManagerContext[Fragment, FragmentManager])
  extends MainStyles with ChartLayout with UiThreading {

  val longString: String = {
    def gen: Stream[String] = Stream.cons("I {fa-heart-o} to {fa-code} on {fa-android}", gen)
    gen.take(300) mkString " "
  }

  lazy val img: Drawable = TR.drawable.material_flat.get
  lazy val candleChartUi = w[CandleStickChart] <~ wire(candleStick) <~ candleStickSettings <~
    vContentSizeMatchWidth(TR.dimen.chartHeight.get)

  def ui: Ui[View] = if (portrait)
    verticalLayout
  else
    candleChartUi <~ vMatchParent

  val mainView: CoordinatorLayout = li.inflate(TR.layout.activity_flexible_fragment)
  val toolbarView: Toolbar = mainView.findView(TR.flexible_toolbar)
  val graphSlot: LinearLayout = mainView.findView(TR.graphSlot)
  var search_view = mainView.findView(TR.search_view)

  def verticalLayout: Ui[View] = {
    insertFragment(f[PairsListFragment])
    Ui(mainView)
  }


  def insertFragment(f: FragmentBuilder[_ <: Fragment]) = {
    replaceFragment(
      builder = f,
      id = TR.flexible_toolbar.id,
      tag = Some(Tag.mainFragment))
  }
}


class MainActivityLayout(implicit cw: ContextWrapper,
                         managerContext: FragmentManagerContext[Fragment, FragmentManager])
  extends MainStyles with ChartLayout with UiThreading {

  lazy val img: Drawable = TR.drawable.material_flat.get
  lazy val candleChartUi = w[CandleStickChart] <~ wire(candleStick) <~ candleStickSettings <~
    vContentSizeMatchWidth(TR.dimen.chartHeight.get)

  def ui: Ui[View] = if (portrait)
    verticalLayout
  else
    candleChartUi <~ vMatchParent

  var toolbarView: Option[Toolbar] = slot[Toolbar]
  var fragmentContent = slot[FrameLayout]
  var materialSearchView = slot[MaterialSearchView]

  def verticalLayout: Ui[View] = {
    l[CoordinatorLayout](
      l[LinearLayout](
        l[AppBarLayout](
          w[Toolbar] <~ wire(toolbarView),
          w[MaterialSearchView] <~ wire(materialSearchView)
        ) <~ vMatchWidth,
        l[FrameLayout]() <~ wire(fragmentContent) <~ id(Id.mainFragment) <~ vMatchParent
      ) <~ vMatchParent <~ vertical
    ) <~ vMatchParent <~ fits
  }

  def insertFragment(f: FragmentBuilder[_ <: Fragment]) = {
    replaceFragment(
      builder = f,
      id = Id.mainFragment,
      tag = Some(Tag.mainFragment))
  }

  def itemSelected(drawerMenuItem: DrawerMenuItem) {
    Ui.run(
      (toolbarView <~ tbTitle(drawerMenuItem.actualToolbarTitle)) //~
//        (drawerLayout <~ dlCloseDrawer(drawerMenu))
    )

    itemAction(drawerMenuItem)
  }

  def itemAction(drawerMenuItem: DrawerMenuItem) : Unit = drawerMenuItem.action()

  case class DrawerMenuItem(title: String, toolbarTitle: Option[String] = None,
                            fragment: Option[FragmentBuilder[_ <: Fragment]] = None,
                            action: () => Any = () => ()){
    def actualToolbarTitle = toolbarTitle.getOrElse(title)
  }
}

trait MainStyles extends UiOperations with Styles with AdditionalTweaks


