package app.bitrader.activity

import android.support.v7.view.ContextThemeWrapper
import android.view.View
import macroid.{ContextWrapper, Ui}

/**
  * Created by Alex Afanasev
  */
object StyleManipulations {

  implicit class ThemedContextWrapper(c: ContextWrapper) {
    def themed(themeResId: Int): ContextWrapper = {
      val newContext = c
      newContext.getOriginal.setTheme(themeResId)
      newContext.application.setTheme(themeResId)
      newContext
    }
  }

  implicit class ContextWrapperCopier(cw: ContextWrapper) {
    def copy: ContextWrapper = ContextWrapper(cw.getOriginal)
  }

  implicit class ThemeWrapper[V <: View](view: => Ui[V]) {
    def theme(theme: Int)(implicit c: ContextWrapper): Ui[V] = {
      applyTheme(ContextWrapper(new ContextThemeWrapper(c.getOriginal, theme)))
    }

    def applyTheme(c: ContextWrapper): Ui[V] = {
      implicit val themedContext: ContextWrapper = c
      view
    }
  }
}

