package app.bitrader.activity

import java.util.Properties

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget._
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.view.LayoutInflaterCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view._
import android.widget.FrameLayout
import app.bitrader.TypedResource._
import app.bitrader._
import app.bitrader.activity.fragments.PairsListFragment
import app.bitrader.activity.layouts.{ChartLayout, DrawerLayout}
import app.bitrader.api.ApiProvider
import app.bitrader.api.common.CurrencyPair
import app.bitrader.helpers._
import app.bitrader.helpers.activity.ActivityOperations
import com.github.mikephil.charting.charts.CandleStickChart
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.miguelcatalan.materialsearchview.MaterialSearchView.OnQueryTextListener
import com.mikepenz.iconics.context.IconicsLayoutInflater
import com.orhanobut.logger.Logger
import macroid.FullDsl._
import macroid._

import scala.language.postfixOps

/**
  * Created by aafa
  */

class MainActivity extends AppCompatActivity with Contexts[AppCompatActivity]
  with ActivityOperations with MenuItems with Circuitable {

  private val chartSub = appCircuit.dataSubscribe(_.chartsData)(layout.updateChartData)
  private val contextZoom = appCircuit.serviceContext
//  private val selectedApiSubscription = appCircuit
//    .subscribe(appCircuit.zoom(_.selectedAccount))(m => updateApi(m.value.api))

  lazy val layout = new MainActivityLayoutInflated(this.getLayoutInflater)
  private lazy val drawer = new DrawerLayout(appCircuit, layout)

  override def onCreate(b: Bundle): Unit = {
    this.setTheme(contextZoom.zoom(_.theme).value)
    LayoutInflaterCompat.setFactory(getLayoutInflater, new IconicsLayoutInflater(getDelegate))

    super.onCreate(b)

    setContentView(layout.ui.get)
    layout.insertFragment(f[PairsListFragment])

    Logger.d("main!")

    //todo setup charts wamp update
    // todo move chart in a separate frame
    //    APIContext.poloniexService(_.currencies()) map layout.updateData
//    layout.updateChartData(appCircuit.serviceData.zoom(_.chartsData).value)
//    appCircuit(UpdateCharts(CurrencyPair.BTC_ETH))
//    appCircuit.subscribe(appCircuit.zoom(_.uiState.mainFragment))(r => r.value map layout.insertFragment)

    setSupportActionBar(layout.toolbarView)
    setTitle(appCircuit.zoom(_.selectedAccount).value.name)

    val drawerSetup = drawer.drawerSetup(this)
  }


  override def onApplyThemeResource(theme: Resources#Theme, resid: Int, first: Boolean): Unit = {
    super.onApplyThemeResource(theme, resid, first)
    println(s"onApplyThemeResource $theme; $resid; $first")
  }

  override def onPause(): Unit = {
    super.onPause()
    chartSub.apply()
//    selectedApiSubscription.apply()
  }


  def updateApi(value: ApiProvider): Unit = {
    startActivity[MainActivity]
    this.finish()
  }
}


trait MenuItems extends AppCompatActivity {
  self: MainActivity =>

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
  val search_view = mainView.findView(TR.search_view)
  val contentFrame: FrameLayout = mainView.findView(TR.content_frame)

  def verticalLayout: Ui[View] = {
    Ui(mainView)
  }

  def insertFragment(f: FragmentBuilder[_ <: Fragment]) = {
    Ui.run(
      replaceFragment(
        builder = f,
        id = TR.content_frame.id,
        tag = Some(Tag.internal_fragment))
    )
  }

}


trait MainStyles extends UiOperations with Styles with AdditionalTweaks
