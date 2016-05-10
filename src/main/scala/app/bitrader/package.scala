package app

import app.bitrader.api.UiService
import app.bitrader.helpers.TweaksAndGoodies
import app.bitrader.helpers.activity.ActivityOperations
import macroid.ContextWrapper
import com.github.nscala_time.time.Imports._
import io.github.aafa.helpers.UiThreading

/**
  * Created by Alexey Afanasev on 17.04.16.
  */
package object bitrader extends ActivityOperations with TweaksAndGoodies with JodaTimeHelpers with CommonTypes {

  implicit class WrapTRContext[A](tr: TypedRes[A]){
    def get(implicit ev: TypedResource.TypedResValueOp[A], c: ContextWrapper): ev.T  =
      tr.value(ev, c.bestAvailable).asInstanceOf[ev.T]
  }
}

trait CommonTypes{
  type OrderPair = (BigDecimal, BigDecimal)
}

trait JodaTimeHelpers{
  implicit class EpochSeconds(dt: DateTime){
    def unixtime : Long  = dt.getMillis / 1000
  }

  implicit class EpochDateTime(unixtime: Long){
    def utime : DateTime  = new DateTime(unixtime * 1000)
    def utimeFormatted : String  = utime.toString("dd.MM.yyyy HH:mm")
  }
}