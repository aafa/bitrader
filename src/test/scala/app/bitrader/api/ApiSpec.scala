package app.bitrader.api

import app.bitrader.api.poloniex.{Poloniex, PoloniexFacade}
import app.bitrader.{AbstractSpec, ClientApplication}

/**
  * Created by Alex Afanasev
  */
abstract class ApiSpec extends AbstractSpec {


  lazy val poloniex: PoloniexFacade = NetworkFacade.factory(Poloniex)
  lazy val poloniexService: UiService[PoloniexFacade] = new UiService(poloniex)

}
