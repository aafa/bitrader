package app.bitrader.helpers

import android.support.design.widget.AppBarLayout.ScrollingViewBehavior
import android.support.design.widget.{CollapsingToolbarLayout, CoordinatorLayout}
import android.support.v7.widget.CardView
import android.view.View
import io.github.aafa.helpers.Styles
import io.github.aafa.macroid.Extensions._
import macroid.FullDsl._
import macroid._


/**
  * Created by Alexey Afanasev on 17.04.16.
  */

trait TweaksAndGoodies extends AppStyles with AdditionalTweaks

trait AppStyles extends Styles{

  def cardTweak(implicit cw: ContextWrapper): Tweak[CardView] = Tweak[CardView](c => {
    val p = 10.dp
    c.setRadius(5.dp)
    c.setCardElevation(3.dp)
    c.setContentPadding(p, p, p, p)
  }) + margin(all = 10.dp)

}


trait AdditionalTweaks {
  def pin(implicit c: ContextWrapper): Tweak[View] = {
    modifyLpTweak[CollapsingToolbarLayout.LayoutParams](
      _.setCollapseMode(CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN)
    )
  }

  def parallax(implicit c: ContextWrapper): Tweak[View] = {
    modifyLpTweak[CollapsingToolbarLayout.LayoutParams](
      _.setCollapseMode(CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX)
    )
  }

  def fits(implicit c: ContextWrapper): Tweak[View] = {
    Tweak[View](_.setFitsSystemWindows(true))
  }

  def nestedScroll(implicit c: ContextWrapper) = modifyLpTweak[CoordinatorLayout.LayoutParams](n => {
    val behavior: ScrollingViewBehavior = new ScrollingViewBehavior
    //        behavior.setOverlayTop(50.dp)
    n.setBehavior(behavior)
  })
}