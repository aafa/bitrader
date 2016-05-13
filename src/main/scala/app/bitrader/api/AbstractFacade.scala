package app.bitrader.api

import java.io.File
import java.text.SimpleDateFormat

import android.content.Context
import app.ObjectEnum
import app.bitrader.TR
import app.bitrader.api.ApiServices.Poloniex
import app.bitrader.api.network.AuthInterceptor
import app.bitrader.api.poloniex.PoloniexFacade
import com.fasterxml.jackson.databind.DeserializationFeature
import com.github.aafa.ScalaRetrofitBuilder
import com.squareup.okhttp.{Cache, OkHttpClient}
import retrofit.RestAdapter

import scala.reflect._

/**
  * Created by Alex Afanasev
  */

sealed trait ApiServices extends ApiServices.Value {
  type ApiFacade

  def facade(implicit ctx: Context): ApiFacade
}

object ApiServices extends ObjectEnum[ApiServices] {

  case object Poloniex extends ApiServices {
    override type ApiFacade = PoloniexFacade

    override def facade(implicit ctx: Context) = new PoloniexFacade
  }

}

trait API {
  type PublicApi
  type PrivateApi
  type WampApi

  val publicApi: PublicApi
  val privateApi: PrivateApi
  val wampApi: WampApi
}

abstract class AbstractFacade(implicit ctx: Context) {

  def buildApi[API: ClassTag](url: String, settings: OkHttpClient => Unit = () => _): API = {
    new CachedRetrofitBuilder(ctx.getApplicationContext.getCacheDir, settings)
      .setEndpoint(url)
      .setLogLevel(RestAdapter.LogLevel.FULL)
      .build()
      .create(classTag[API].runtimeClass).asInstanceOf[API]
  }

}


object NetworkFacadeFactory {
  def factory(s: ApiServices)(implicit ctx: Context): s.ApiFacade = s.facade
}


class CachedRetrofitBuilder(cacheDir: File, settings: OkHttpClient => Unit = () => _) extends ScalaRetrofitBuilder(
  scalaMapperSettings = { om =>
    om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
  },
  okClientSettings = { ok =>
    settings(ok)
    ok.setCache(new Cache(cacheDir, 10 * 1024 * 1024))
  }
)