package app.bitrader.api

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import android.content.Context
import app.ObjectEnum
import app.bitrader.api.common.CurrencyPair
import app.bitrader.api.poloniex.{Chart, OrdersBook, TradeHistory}
import com.fasterxml.jackson.databind.DeserializationFeature
import com.github.aafa.ScalaRetrofitBuilder
import com.squareup.okhttp.{Cache, OkHttpClient}
import retrofit.RestAdapter

import scala.reflect._

/**
  * Created by Alex Afanasev
  */

private[bitrader] trait ApiService extends ApiService.Value {
  type ApiFacade

  def facade(implicit ctx: Context): ApiFacade
}

private[bitrader] object ApiService extends ObjectEnum[ApiService]

sealed trait API {
  type PublicApi
  type PrivateApi
  type WampApi

  val publicApi: PublicApi
  val privateApi: PrivateApi
  val wampApi: WampApi
}

abstract class AbstractFacade(implicit ctx: Context) extends API {
  def nonce: String = new Date().getTime.toString

  protected def buildApi[API: ClassTag](url: String, settings: OkHttpClient => Unit = () => _): API = {
    new CachedRetrofitBuilder(ctx.getApplicationContext.getCacheDir, settings)
      .setEndpoint(url)
      .setLogLevel(RestAdapter.LogLevel.FULL)
      .build()
      .create(classTag[API].runtimeClass).asInstanceOf[API]
  }

  private class CachedRetrofitBuilder(cacheDir: File, settings: OkHttpClient => Unit = () => _)
    extends ScalaRetrofitBuilder(
      scalaMapperSettings = { om =>
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
      },
      okClientSettings = { ok =>
        settings(ok)
        ok.setCache(new Cache(cacheDir, 10 * 1024 * 1024))
      }
    )

  def chartData(pair: CurrencyPair, start: Long, end: Long, period: Int): Seq[Chart]

  def ordersBook(pair: CurrencyPair, depth: Int): OrdersBook

  def tradeHistory(pair: CurrencyPair): Seq[TradeHistory]
}

object NetworkFacade {
  def factory(s: ApiService)(implicit ctx: Context): s.ApiFacade = s.facade
}

