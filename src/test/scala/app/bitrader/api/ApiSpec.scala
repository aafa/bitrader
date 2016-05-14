package app.bitrader.api

import app.bitrader.{AbstractSpec, ClientApplication, TR}
import app.bitrader.api.poloniex.{PoloniexFacade, PoloniexPublicAPI, PoloniexTradingAPI}
import org.robolectric.RuntimeEnvironment
import org.robolectric.res.{Fs, FsFile}
import org.scalatest.{FlatSpec, Matchers, RobolectricSuite}

/**
  * Created by Alex Afanasev
  */
abstract class ApiSpec extends AbstractSpec {

  override val aarsDir: FsFile = Fs.currentDirectory().join("target/android/intermediates/aars")

  class TestApplication extends ClientApplication
  lazy val poloniex: PoloniexFacade = NetworkFacade.factory(Poloniex)
  lazy val poloniexService: UiService[PoloniexFacade] = new UiService(poloniex)

}
