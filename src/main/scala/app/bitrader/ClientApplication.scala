package app.bitrader

import java.io.File
import java.text.SimpleDateFormat

import android.app.Application
import android.content.Context
import app.bitrader.api.{ApiServices, NetworkFacade, UiService}
import app.bitrader.api.network.AuthInterceptor
import app.bitrader.api.poloniex.{PoloniexFacade, PoloniexPublicAPI}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.github.aafa.ScalaRetrofitBuilder
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.{FontAwesomeModule, MaterialModule}
import com.squareup.okhttp.Cache
import retrofit.RestAdapter

import scala.reflect.{ClassTag, _}

/**
  * Created by Alexey Afanasev on 07.04.16.
  */
class ClientApplication extends Application {

  override def onCreate(): Unit = {
    super.onCreate()
    Iconify.`with`(new FontAwesomeModule).`with`(new MaterialModule)

    implicit val c = getApplicationContext
    APIContext.poloniexApi = NetworkFacade.factory(ApiServices.Poloniex)
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

