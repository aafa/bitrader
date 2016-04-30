package app.bitrader.api

import java.util.Date

import android.os.Build.VERSION_CODES._
import app.bitrader.api.poloniex.{CurrencyPair, OrdersBook, TradeHistory}
import org.robolectric.annotation.Config

/**
  * Created by Alex Afanasev
  */
@Config(sdk = Array(LOLLIPOP))
class TradeHistorySpec extends ApiSpec {

  def assertHistory(history: Seq[TradeHistory]): Unit = {
    history map (th => {
      assert(th.globalTradeID > 0)
      assert(th.tradeID > 0)
      assert(th.date.before(new Date))
    })
  }

  it should "work with orders book" in {
    val history: Seq[TradeHistory] = poloniexApi.tradeHistory(CurrencyPair.BTC_ETH)
    assertHistory(history)
  }


}