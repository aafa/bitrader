package app.bitrader.activity.layouts

import android.graphics.drawable.Drawable
import android.support.design.widget.{AppBarLayout, CoordinatorLayout}
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.{FrameLayout, LinearLayout}
import app.bitrader.TR
import app.bitrader.activity.MainStyles
import app.bitrader.helpers.{Id, UiThreading}
import com.fortysevendeg.macroid.extras.ToolbarTweaks.tbTitle
import com.github.mikephil.charting.charts.CandleStickChart
import com.miguelcatalan.materialsearchview.MaterialSearchView
import macroid.FullDsl._
import macroid._
import macroid.{ContextWrapper, FragmentBuilder, FragmentManagerContext, Tag, Ui}

/**
  * Created by Alex Afanasev
  */
class MainActivityLayoutIncode(implicit cw: ContextWrapper,
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

  val verticalLayout: Ui[View] = {
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
