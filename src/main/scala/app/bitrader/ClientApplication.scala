package app.bitrader

import android.app.Application
import android.content.Context
import app.bitrader.api.poloniex.{Poloniex, PoloniexFacade}
import app.bitrader.api.{AbstractFacade, ApiService, NetworkFacade, UiService}
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
    APIContext.appContext = c
    APIContext.apis = Map(
        Poloniex -> NetworkFacade.factory(Poloniex)
    )

  }

}

object APIContext {
  lazy val jacksonMapper = {
    val jm = new ObjectMapper() with ScalaObjectMapper
    jm.registerModule(DefaultScalaModule)
    jm.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    jm
  }

  var apis: Map[ApiService, AbstractFacade] = Map.empty
  var appContext: Context = _

  def getService(apiService: ApiService): UiService[AbstractFacade] = new UiService(get(apiService))
  def get(apiService: ApiService): AbstractFacade = apis(apiService)
}

