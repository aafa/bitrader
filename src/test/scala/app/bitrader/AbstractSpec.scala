package app.bitrader

import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers, RobolectricSuite}

/**
  * Created by Alex Afanasev
  */
abstract class AbstractSpec extends FlatSpec with Matchers with RobolectricSuite{

  implicit class TimePrint[T](f: => T){
    def printTime : T = {
      val tic = DateTime.now
      val res = f
      val tac = DateTime.now
      println("time elapsed %.2f sec".format((tac.getMillis - tic.getMillis) / 1000f))
      res
    }
  }

}
