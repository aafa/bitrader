package app.bitrader

import android.os.Build.VERSION_CODES._
import app.bitrader.api.apitest.ApiTest
import app.bitrader.api.bitfinex.Bitfinex
import app.bitrader.api.poloniex.Poloniex
import macroid.ContextWrapper
import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.res.{Fs, FsFile}
import org.robolectric.{RobolectricTestRunner, RuntimeEnvironment}
import org.scalatest.{FlatSpec, Matchers, RobolectricSuite}

/**
  * Created by Alex Afanasev
  */
@Config(sdk = Array(LOLLIPOP),
  constants = classOf[BuildConfig],
  manifest = "src/main/AndroidManifest.xml",
  application = classOf[TestApplication])
@RunWith(classOf[RobolectricTestRunner])
abstract class AbstractSpec extends FlatSpec with Matchers with RobolectricSuite {

  // todo https://philio.me/android-data-binding-with-robolectric-3/

//  override val aarsDir: FsFile = Fs.fileFromPath("/Users/aafa/.android/sbt/exploded-aars")

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

class TestApplication extends ClientApplication {
  override val diModule = DiModuleTest
}

object DiModuleTest extends DiModule {
  val appCircuit = new AppCircuitTest
}

class AppCircuitTest extends AppCircuit {
  override def initialModel = RootModel(
    ApiTest,
    UiState(),
    Map(
      ApiTest -> ServiceContext(theme = R.style.MainTheme)
    ))
}
