package app.bitrader.helpers

import android.support.v4.app.{Fragment, FragmentManager}
import macroid.FullDsl._
import macroid._
import macroid.{ContextWrapper, FragmentBuilder, FragmentManagerContext, Ui}

/**
  * Created by Alexey Afanasev
  */
trait UiOperations {
  def replaceFragment[F <: Fragment](
                                      builder: FragmentBuilder[F],
                                      id: Int,
                                      tag: Option[String] = None)
                                    (implicit managerContext: FragmentManagerContext[Fragment, FragmentManager]) = {
    builder.factory.map (managerContext.manager.beginTransaction().replace(id, _, tag.orNull).commit())
  }

  def showToast(txt: String)(implicit cw: ContextWrapper): Any = Ui.run(toast(txt) <~ fry)
}
