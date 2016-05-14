package app.bitrader

import android.app.Application
import app.bitrader.api.poloniex.PoloniexFacade
import app.bitrader.api.{NetworkFacade, Poloniex, UiService}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.{FontAwesomeModule, MaterialModule}

/**
  * Created by Alexey Afanasev on 07.04.16.
  */
class ClientApplication extends Application {

  override def onCreate(): Unit = {
    super.onCreate()
    Iconify.`with`(new FontAwesomeModule).`with`(new MaterialModule)

    implicit val c = getApplicationContext
    APIContext.poloniexApi = NetworkFacade.factory(Poloniex)
  }

}

// todo factory
object APIContext {
  lazy val jacksonMapper = {
    val jm = new ObjectMapper() with ScalaObjectMapper
    jm.registerModule(DefaultScalaModule)
    jm.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    jm
  }

  var poloniexApi: PoloniexFacade = _
  def poloniexService: UiService[PoloniexFacade] = new UiService(poloniexApi)
}

