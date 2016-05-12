package app.bitrader

import java.io.File
import java.text.SimpleDateFormat

import android.app.Application
import android.content.Context
import app.bitrader.api.UiService
import app.bitrader.api.poloniex.PoloniexAPIServiceDescriptor
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
    APIContext.poloniexApi = buildApi[PoloniexAPIServiceDescriptor](TR.string.poloniex_url.value)
  }

  def buildApi[API : ClassTag](url: String)(implicit ctx: Context): API = {
    new CachedRetrofitBuilder(ctx.getApplicationContext.getCacheDir)
      .setEndpoint(url)
      .setLogLevel(RestAdapter.LogLevel.FULL)
      .build()
      .create(classTag[API].runtimeClass).asInstanceOf[API]
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

  var poloniexApi: PoloniexAPIServiceDescriptor = _
  def poloniexService: UiService[PoloniexAPIServiceDescriptor] = new UiService(poloniexApi)
}

class CachedRetrofitBuilder(cacheDir: File) extends ScalaRetrofitBuilder(
  scalaMapperSettings = { om =>
    om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
  },
  okClientSettings = { ok =>
    ok.setCache(new Cache(cacheDir, 10*1024*1024))
  }
)