package app.bitrader.activity

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget._
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.{CardView, Toolbar}
import android.view.{Gravity, View}
import android.widget.LinearLayout
import app.bitrader.TR
import app.bitrader.helpers.Id
import com.joanzapata.iconify.widget.IconTextView
import io.github.aafa.toolbar.ToolbarStyle
import macroid.FullDsl._
import macroid._

/**
  * Created by aafa
  */

class MainActivity extends AppCompatActivity with Contexts[AppCompatActivity] with MainActivityView {

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    setContentView(ui.get)

    toolbarTitle("Bitrader")
  }

}

trait MainActivityView extends ToolbarStyle {
  this: Contexts[AppCompatActivity] =>

  type CL = CoordinatorLayout.LayoutParams

  def fits: Tweak[View] = {
    Tweak[View](v => {
      v.setFitsSystemWindows(true)
    })
  }

  val longString: String = {
    def gen: Stream[String] = Stream.cons("I {fa-heart-o} to {fa-code} on {fa-android}", gen)
    gen.take(300) mkString " "
  }

  def cardTweak: Tweak[CardView] = Tweak[CardView](c => {
    val p = 10.dp
    c.setRadius(5.dp)
    c.setCardElevation(4.dp)
    c.setContentPadding(p, p, p, p)
  }) + margin(all = 10.dp)

  def img: Drawable = TR.drawable.material_flat.get

  def ui: Ui[CoordinatorLayout] = {
    l[CoordinatorLayout](
      l[AppBarLayout](
        l[CollapsingToolbarLayout](
          w[Toolbar] <~ wire(toolBar) <~ vContentSizeMatchWidth(TR.dimen.toolbar_height.get) <~ pin
        ) <~ vMatchParent <~ scrollFlags <~ ctlTweak
      ) <~ id(Id.appbar) <~ vContentSizeMatchWidth(180.dp),

      l[NestedScrollView](
        l[LinearLayout](
          l[CardView](
            l[LinearLayout](
              w[IconTextView] <~ text(longString)
            )
          ) <~ vMatchWidth <~ cardTweak <~ id(Id.card)
        )
      ) <~ vMatchParent <~ nestedScroll(40.dp)

//      w[FloatingActionButton] <~ drawable(AwesomeIcon(FontAwesomeIcons.fa_android)) <~ fabTweak <~ onClick(
//        Ui.run(toast("fabTweak") <~ fry)
//      )
    ) <~ vMatchParent <~ fitsAll
  }

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

  def fabTweak = modifyLpTweak[CL](params => {
    params.anchorGravity = Gravity.TOP | Gravity.RIGHT | Gravity.END
    params.setAnchorId(Id.card)
    params.setMarginEnd(20.dp)
  })

}
