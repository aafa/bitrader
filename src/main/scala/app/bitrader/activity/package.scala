package app.bitrader

import app.bitrader.helpers.{ActivityOperations, AppStyles, Dialogs, TweaksAndGoodies}
import macroid.ContextWrapper

/**
  * Created by Alexey Afanasev on 17.04.16.
  */
package object activity extends ActivityOperations with TweaksAndGoodies{

  implicit class WrapTRContext[A](tr: TypedRes[A]){
    def get(implicit ev: TypedResource.TypedResValueOp[A], c: ContextWrapper): ev.T  =
      tr.value(ev, c.bestAvailable).asInstanceOf[ev.T]
  }

}
