package app.bitrader

import android.app.Application
import android.content.Context
import app.bitrader.api._
import app.bitrader.api.apitest.ApiTest
import app.bitrader.api.poloniex.Poloniex
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.{FontAwesomeModule, MaterialModule}
import com.orhanobut.logger.Logger

/**
  * Created by Alexey Afanasev on 07.04.16.
  */
class ClientApplication extends Application {

  val diModule: DiModule = DiModuleProd

  override def onCreate(): Unit = {
    super.onCreate()
    Iconify.`with`(new FontAwesomeModule).`with`(new MaterialModule)

    Logger.init("Bitrader")

    AppContext.appContext = getApplicationContext
    AppContext.diModule = diModule
  }

}

object AppContext {

  implicit var appContext: Context = _
  var diModule : DiModule  = _

  lazy val jacksonMapper = {
    val jm = new ObjectMapper() with ScalaObjectMapper
    jm.registerModule(DefaultScalaModule)
    jm.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    jm
  }

  // todo memoize smart
  lazy val apis: Map[ApiProvider, AbstractFacade] = Map(
    Poloniex -> NetworkFacade.factory(Poloniex),
    ApiTest -> NetworkFacade.factory(ApiTest)
//    Bitfinex -> NetworkFacade.factory(Bitfinex)
  )

  def getService(api: ApiProvider): UiService[AbstractFacade] = new UiService(get(api))
  def get(api: ApiProvider): AbstractFacade = apis(api)
}

trait DiModule {
  val appCircuit : ICircuit
}

object DiModuleProd extends DiModule{
  val appCircuit = new AppCircuit
}