package app.bitrader.helpers

import com.joanzapata.iconify.{Icon, IconDrawable}
import macroid.ContextWrapper
import app.bitrader.R

/**
  * Created by Alexey Afanasev on 08.04.16.
  */
object AwesomeIcon {
  def apply(ic: Icon)(implicit c: ContextWrapper): IconDrawable =
    new IconDrawable(c.bestAvailable, ic).colorRes(R.color.toolbar_title).actionBarSize()
}
