package app.bitrader.helpers.activity

import android.graphics.{PorterDuff, PorterDuffColorFilter}
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.{ActionMenuView, ImageButton, TextView}
import io.github.aafa.macroid.AdditionalTweaks
import macroid.{ContextWrapper, Transformer}
import macroid._

/**
  * Created by Alex Afanasev
  */
trait ActivityStyles extends AdditionalTweaks{

  def colorizeToolbar(toolbar: Toolbar, color: Int) = {
    val filter: PorterDuffColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)

    toolbar.setTitleTextColor(color)
    toolbar.setSubtitleTextColor(color)

    println("applyThemeColors " + toolbar.getTitle)

    for (i <- 0 to toolbar.getChildCount) {
      val view: View = toolbar.getChildAt(i)
      println("apply styles view " + view)

      view match {
        case v: ImageButton => v.getDrawable.setColorFilter(filter)
        case t: TextView => t.setTextColor(color)
        case am: ActionMenuView =>
        case _ =>
      }
    }
  }

  def fitsAll(implicit c: ContextWrapper): Transformer = {
    Transformer {
      case a => a <~ fits
    }
  }

}
