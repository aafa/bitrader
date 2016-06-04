package app.bitrader.api

import app.bitrader.api.poloniex.TradeHistory

/**
  * Created by Alex Afanasev
  */
class MakeOrderSpec extends ApiSpec{

  it should "make orders" in {
    val orders = poloniex.returnOpenOrders()
    println(orders)
  }

  it should "have history" in {
    val history = poloniex.myTradeHistory()
    println(history)
  }
}
