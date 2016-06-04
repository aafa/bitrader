package app.bitrader.api

import app.bitrader.api.common.CurrencyPair
import app.bitrader.api.poloniex.{ActualOrder, OrderDetails, TradeHistory}

/**
  * Created by Alex Afanasev
  */
class MakeOrderSpec extends ApiSpec {

  it should "make orders" in {

    assume(poloniex.balances.exists {
      case (coin, amount) => coin == "ETH" && amount.toDouble > 0
    })

    // assuming we have eth
    val buyResult: ActualOrder = poloniex.sell(CurrencyPair.BTC_ETH.toString, 999, 0.5)
    println(buyResult)

    assert(checkCurrentOrders.nonEmpty)

    poloniex.cancelOrder(buyResult.orderNumber)

    assert(checkCurrentOrders.isEmpty)
  }

  def checkCurrentOrders: Map[String, Seq[OrderDetails]] = {
    val orders = poloniex.returnOpenOrders()
    val actualOrders: Map[String, Seq[OrderDetails]] = orders.filter {
      case (_, list) => list.nonEmpty
    }

    println(actualOrders)
    actualOrders
  }

  it should "have history" in {
    val history = poloniex.myTradeHistory()
    println(history)
  }
}
