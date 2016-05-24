package app.bitrader

import android.app.Application
import android.content.Context
import app.bitrader.api.poloniex.{Poloniex, PoloniexFacade}
import app.bitrader.api._
import app.bitrader.api.bitfinex.Bitfinex
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

    APIContext.appContext = getApplicationContext
  }

}

object APIContext {
  implicit var appContext: Context = _

  lazy val jacksonMapper = {
    val jm = new ObjectMapper() with ScalaObjectMapper
    jm.registerModule(DefaultScalaModule)
    jm.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    jm
  }

  lazy val apis: Map[ApiProvider, AbstractFacade] = Map(
    Poloniex -> NetworkFacade.factory(Poloniex),
    Bitfinex -> NetworkFacade.factory(Bitfinex)
  )

  def getService(api: ApiProvider): UiService[AbstractFacade] = new UiService(get(api))
  def get(api: ApiProvider): AbstractFacade = apis(api)
}

