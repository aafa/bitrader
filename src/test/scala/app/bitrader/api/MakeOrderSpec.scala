package app.bitrader.api

import app.bitrader.api.common.CurrencyPair
import app.bitrader.api.poloniex.{ActualOrder, OrderDetails, TradeHistory}

/**
  * Created by Alex Afanasev
  */
class MakeOrderSpec extends ApiSpec{

  it should "make orders" in {
    checkCurrentOrders

    val buyResult: ActualOrder = poloniex.sell(CurrencyPair.BTC_ETH.toString, 999, 0.5)
    println(buyResult)

    assert(checkCurrentOrders.keys.nonEmpty)

    poloniex.cancelOrder(buyResult.orderNumber)

    assert(checkCurrentOrders.keys.isEmpty)
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
