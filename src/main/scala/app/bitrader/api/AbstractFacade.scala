package app.bitrader.api

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import android.content.Context
import app.ObjectEnum
import app.bitrader.AppCircuit
import app.bitrader.api.common.CurrencyPair.CurrencyPair
import app.bitrader.api.common.WampMsg
import app.bitrader.api.network.{JawampaClient, WampSub}
import app.bitrader.api.poloniex.{Chart, OrdersBook, Ticker, TradeHistory}
import com.fasterxml.jackson.databind.DeserializationFeature
import com.github.aafa.ScalaRetrofitBuilder
import com.squareup.okhttp.{Cache, OkHttpClient}
import retrofit.RestAdapter
import retrofit.RestAdapter.Log

import scala.reflect._

/**
  * Created by Alex Afanasev
  */

private[bitrader] trait ApiProvider extends ApiProvider.Value {
  type ApiFacade

  def facade(implicit ctx: Context): ApiFacade
}

private[bitrader] object ApiProvider extends ObjectEnum[ApiProvider]

sealed trait API {
  type PublicApi
  type PrivateApi
  type WampApi = JawampaClient // todo custom trait
  type OkHttpApi

  val publicApi: PublicApi
  val privateApi: PrivateApi
  val wampApi: WampApi
  val okHttp: OkHttpApi
}

abstract class AbstractFacade(implicit ctx: Context) extends API {
  def nonce: String = new Date().getTime.toString

  // public

  def returnTicket: Map[String, Ticker]

  def chartData(pair: CurrencyPair, start: Long, end: Long, period: Int): Seq[Chart]

  def ordersBook(pair: CurrencyPair, depth: Int): OrdersBook

  def tradeHistory(pair: CurrencyPair): Seq[TradeHistory]

  def wampSubscribe[WM <: WampMsg : scala.reflect.Manifest](subs: WampSub[WM]): Unit

  def wampClose: Unit

  // private

  def balances: Map[String, String]

  // build stuff

  protected def buildApi[API: ClassTag](url: String, settings: OkHttpClient => Unit = () => _): API = {
    new CachedRetrofitBuilder(ctx.getApplicationContext.getCacheDir, settings)
      .setEndpoint(url)
      .setLogLevel(RestAdapter.LogLevel.FULL)
//      .setLog(new Log {
//        override def log(message: String): Unit = println(message)
//      })
      .build()
      .create(classTag[API].runtimeClass).asInstanceOf[API]
  }

  protected def buildWamp(url: String) = new JawampaClient(url, AppCircuit)

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

}

object NetworkFacade {
  def factory(s: ApiProvider)(implicit ctx: Context): s.ApiFacade = s.facade
}

