package app.bitrader.api

import app.bitrader.api.poloniex.{Poloniex, PoloniexFacade}
import app.bitrader.{AbstractSpec, ClientApplication}
import org.robolectric.res.{Fs, FsFile}

/**
  * Created by Alex Afanasev
  */
abstract class ApiSpec extends AbstractSpec {


  class TestApplication extends ClientApplication
  lazy val poloniex: PoloniexFacade = NetworkFacade.factory(Poloniex)
  lazy val poloniexService: UiService[PoloniexFacade] = new UiService(poloniex)

}
