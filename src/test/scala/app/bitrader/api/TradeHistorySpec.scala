package app.bitrader.api

import java.util.Date

import app.bitrader.api.common.CurrencyPair
import app.bitrader.api.poloniex.TradeHistory

/**
  * Created by Alex Afanasev
  */
class TradeHistorySpec extends ApiSpec {

  def assertHistory(history: Seq[TradeHistory]): Unit = {
    history map (th => {
      assert(th.globalTradeID.nonEmpty)
      assert(th.tradeID.nonEmpty)
//      assert(th.date.before(new Date))
    })
  }

  it should "work with orders book" in {
    val history: Seq[TradeHistory] = poloniex.tradeHistory(CurrencyPair.BTC_ETH)
    assertHistory(history)
  }


}
