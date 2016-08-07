package app.bitrader.helpers

import android.support.design.widget.AppBarLayout.ScrollingViewBehavior
import android.support.design.widget.{CollapsingToolbarLayout, CoordinatorLayout}
import android.view.View
import android.view.ViewGroup.LayoutParams
import macroid.{ContextWrapper, Tweak}

import scala.reflect._

/**
  * Created by Alex Afanasev
  */
trait AdditionalTweaks extends ModifyViewLayout{
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

  def nestedScroll(overlayTop : Int = 0)(implicit c: ContextWrapper) = modifyLpTweak[CoordinatorLayout.LayoutParams](n => {
    val behavior: ScrollingViewBehavior = new ScrollingViewBehavior
    behavior.setOverlayTop(overlayTop)
    n.setBehavior(behavior)
  })
}

trait ModifyViewLayout {
  implicit class ModifyViewLP(view: View) {
    def modifyLP[LP <: LayoutParams : ClassTag](modify: LP => Unit): Unit = {
      view.getLayoutParams match {
        case lp: LP => update(modify, lp)
        case lp: LayoutParams => update(modify, create(lp))
        case _ => update(modify, create())
      }
    }

    def update[LP <: LayoutParams : ClassTag](modify: (LP) => Unit, lp: LP): Unit = {
      modify(lp)
      view.setLayoutParams(lp)
    }

    def create[LP <: LayoutParams : ClassTag](lp: LayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)): LP = {
      classTag[LP].runtimeClass
        .getConstructor(classOf[LayoutParams])
        .newInstance(lp)
        .asInstanceOf[LP]
    }
  }

  def modifyLpTweak[LP <: LayoutParams : ClassTag](modify: LP => Unit): Tweak[View] = Tweak[View](_.modifyLP(modify))
}
