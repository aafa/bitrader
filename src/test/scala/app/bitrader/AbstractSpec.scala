package app.bitrader

import android.os.Build.VERSION_CODES._
import macroid.ContextWrapper
import org.joda.time.DateTime
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.scalatest.{FlatSpec, Matchers, RobolectricSuite}

/**
  * Created by Alex Afanasev
  */
@Config(sdk = Array(LOLLIPOP))
abstract class AbstractSpec extends FlatSpec with Matchers with RobolectricSuite {

  implicit val cw = ContextWrapper(RuntimeEnvironment.application)

  implicit class TimePrint[T](f: => T) {
    def printTime: T = {
      val tic = DateTime.now
      val res = f
      val tac = DateTime.now
      println("time elapsed %.2f sec".format((tac.getMillis - tic.getMillis) / 1000f))
      res
    }
  }

}
