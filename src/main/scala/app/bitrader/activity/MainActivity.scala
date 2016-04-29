package app.bitrader.activity

import java.util.Date

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget._
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.widget.{DrawerLayout, NestedScrollView}
import android.support.v7.widget.{CardView, Toolbar}
import android.view.Gravity
import android.widget.LinearLayout
import app.bitrader.api.poloniex.Chart
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

/**
  * Created by aafa
  */

class MainActivity extends DrawerActivity {

  override lazy val layout = new MainActivityLayout(menuItems)

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    setContentView(layout.ui.get)

    APIContext.poloniexService(_.returnTicker()) mapUi update
    APIContext.poloniexService(_.chartData("BTC_ETH", 2.days.ago().unixtime, DateTime.now.unixtime, 300)) map updateChart
  }

  def updateChart(chartData: Seq[Chart]): Unit = {
    val xs: Seq[String] = chartData map (chart => new Date(chart.date).formatted("YYYY.mm.DD"))
    val ys: Seq[CandleEntry] = chartData map (chart => new CandleEntry(chartData.indexOf(chart), chart.high.floatValue(), chart.low.floatValue(), chart.open.floatValue(), chart.close.floatValue()))
    val set = new CandleDataSet(ys.asJava, "Data")

    println("xs" + xs)
    println("ys" + ys)

    val data: CandleData = new CandleData(xs.asJava, set)
    Ui.run(layout.updateChart(data))
  }

  def update(t: Map[_, _]): Ui[Unit] = {
    Ui.run(
      layout.textSlot <~ text(t.toString()),
      layout.toolBar <~ Tweak[Toolbar](_.setTitle("got it!"))
    )
    Ui.nop
  }

  val menuItems: Seq[DrawerMenuItem] = Seq(
    DrawerMenuItem("Account")
  )
}

class MainActivityLayout(override val menuItems: Seq[DrawerMenuItem])
                        (implicit cw: ContextWrapper, managerContext: FragmentManagerContext[Fragment, FragmentManager])
  extends BasicDrawerLayout(menuItems) with MainStyles with ChartStyles{


  var textSlot = slot[IconTextView]

  val longString: String = {
    def gen: Stream[String] = Stream.cons("I {fa-heart-o} to {fa-code} on {fa-android}", gen)
    gen.take(300) mkString " "
  }

  def img: Drawable = TR.drawable.material_flat.get

  def ui: Ui[DrawerLayout] = {
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
                w[IconTextView] <~ text("test")
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
    c.setCardElevation(4.dp)
    c.setContentPadding(p, p, p, p)
  }) + margin(all = 10.dp)

}

trait MainStyles extends ToolbarAboveLayout with UiOperations with Styles with AdditionalTweaks{

}

trait ChartStyles {
  var candleStick = slot[CandleStickChart]

  def updateChart(data: CandleData): Ui[_] = {
    candleStick <~ Tweak[CandleStickChart](cs => {
      cs.setData(data)
      cs.invalidate()
    })
  }

  val candleStickSettings = Tweak[CandleStickChart](
    _.setDescription("Bitrader data")

  )
}
