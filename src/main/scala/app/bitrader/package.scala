package app

import app.bitrader.api.UiService
import app.bitrader.helpers.{ActivityOperations, TweaksAndGoodies}
import macroid.ContextWrapper
import com.github.nscala_time.time.Imports._

/**
  * Created by Alexey Afanasev on 17.04.16.
  */
package object bitrader extends ActivityOperations with TweaksAndGoodies{

  implicit class WrapTRContext[A](tr: TypedRes[A]){
    def get(implicit ev: TypedResource.TypedResValueOp[A], c: ContextWrapper): ev.T  =
      tr.value(ev, c.bestAvailable).asInstanceOf[ev.T]
  }

  implicit class EpochSeconds(dt: DateTime){
    def unixtime : Long  =
      dt.getMillis / 1000
  }

}
