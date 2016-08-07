package app.bitrader.helpers

import android.support.v4.app.{Fragment, FragmentManager}
import macroid.{FragmentBuilder, FragmentManagerContext}

/**
  * Created by Alexey Afanasev on 15.02.16.
  */
trait UiOperations {
  def replaceFragment[F <: Fragment](
                                      builder: FragmentBuilder[F],
                                      id: Int,
                                      tag: Option[String] = None)
                                    (implicit managerContext: FragmentManagerContext[Fragment, FragmentManager]) = {
    builder.factory.map (managerContext.manager.beginTransaction().replace(id, _, tag.orNull).commit())
  }
}
