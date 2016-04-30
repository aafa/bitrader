package app.bitrader.api

import app.bitrader.ClientApplication
import app.bitrader.api.poloniex.PoloniexAPIServiceDescriptor
import org.robolectric.RuntimeEnvironment
import org.robolectric.res.{Fs, FsFile}
import org.scalatest.{FlatSpec, Matchers, RobolectricSuite}

/**
  * Created by Alex Afanasev
  */
abstract class ApiSpec extends FlatSpec with Matchers with RobolectricSuite{

  override val aarsDir: FsFile = Fs.currentDirectory().join("target/android/intermediates/aars")

  class TestApplication extends ClientApplication
  lazy val poloniexApi: PoloniexAPIServiceDescriptor = new TestApplication().buildApi(RuntimeEnvironment.application)

  lazy val poloniexService: UiService[PoloniexAPIServiceDescriptor] = new UiService(poloniexApi)

}
