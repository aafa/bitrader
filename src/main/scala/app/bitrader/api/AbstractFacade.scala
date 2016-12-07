package app.bitrader.api

import java.util.Date

import android.content.Context
import app.ObjectEnum
import app.bitrader.AppCircuit
import app.bitrader.activity.Circuitable
import app.bitrader.api.common.CurrencyPair.CurrencyPair
import app.bitrader.api.common.WampMsg
import app.bitrader.api.network.{JawampaClient, WampSub}
import app.bitrader.api.poloniex._

/**
  * Created by Alex Afanasev
  */

private[bitrader] trait ApiProvider extends ApiProvider.Value {
  type ApiFacade

  def facade(implicit ctx: Context): ApiFacade
}

private[bitrader] object ApiProvider extends ObjectEnum[ApiProvider]

abstract class AbstractFacade(implicit ctx: Context) extends Circuitable{
  def nonce: String = new Date().getTime.toString

  // public

  def returnTicket: Map[String, Any]

  def chartData(pair: CurrencyPair, start: Long, end: Long, period: Int): Seq[Chart]

  def ordersBook(pair: CurrencyPair, depth: Int): OrdersBook

  def ordersBook(depth: Int): Map[String, OrdersBook]

  def tradeHistory(pair: CurrencyPair): Seq[TradeHistory]

  def wampSubscribe[WM <: WampMsg : scala.reflect.Manifest](subs: WampSub[WM]): Unit

  def wampClose: Unit

  def currencies(): Map[String, Currency]

  // private

  def balances: Map[String, String]

  // build stuff


  protected def buildWamp(url: String) = new JawampaClient(url, appCircuit)

//  private class CachedRetrofitBuilder(cacheDir: File, settings: OkHttpClient => Unit = () => _)
//    extends ScalaRetrofitBuilder(
//      scalaMapperSettings = { om =>
//        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))  // todo reuse this date pattern ?
//      },
//      okClientSettings = { ok =>
//        settings(ok)
//        ok.setCache(new Cache(cacheDir, 10 * 1024 * 1024))
//      }
//    )

}

object NetworkFacade {
  def factory(s: ApiProvider)(implicit ctx: Context): s.ApiFacade = s.facade
}

