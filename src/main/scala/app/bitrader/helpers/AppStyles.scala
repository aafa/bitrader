package app.bitrader.helpers

import android.support.design.widget.AppBarLayout.ScrollingViewBehavior
import android.support.design.widget.{CollapsingToolbarLayout, CoordinatorLayout}
import android.support.v7.widget.CardView
import android.view.View
import io.github.aafa.helpers.Styles
import io.github.aafa.macroid.AdditionalTweaks
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

