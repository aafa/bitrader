package app.bitrader

import android.os.Build.VERSION_CODES._
import macroid.ContextWrapper
import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.robolectric.{RobolectricTestRunner, RuntimeEnvironment}
import org.robolectric.annotation.Config
import org.robolectric.res.{Fs, FsFile}
import org.scalatest.{FlatSpec, Matchers, RobolectricSuite}

/**
  * Created by Alex Afanasev
  */
@Config(sdk = Array(LOLLIPOP), constants = classOf[BuildConfig], manifest = "src/main/AndroidManifest.xml")
@RunWith(classOf[RobolectricTestRunner])
abstract class AbstractSpec extends FlatSpec with Matchers with RobolectricSuite {

  override val aarsDir: FsFile = {
    val file: FsFile = Fs.fileFromPath("/Users/aafa/.android/sbt/exploded-aars")
    print("!! aarsDir: " + file.getPath)
    file
  }

  implicit val cw = ContextWrapper(RuntimeEnvironment.application)
  implicit val c = RuntimeEnvironment.application

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
