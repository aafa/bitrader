package app.bitrader.api

import app.bitrader._
import app.bitrader.api.common.CurrencyPair
import app.bitrader.api.poloniex.{OrdersBook, Ticker}

/**
  * Created by Alex Afanasev
  */
class OrdersBookSpec extends ApiSpec {

  it should "read ticker" in {
    val tickets: Map[String, Ticker] = poloniex.returnTicket
    assert(tickets.nonEmpty)
  }


  it should "work with orders book" in {
    val ordersBook: OrdersBook = poloniex.ordersBook(CurrencyPair.BTC_ETH, 20)
    assertOrder(ordersBook)
  }

  it should "work with BIG orders book" in {
    val ordersBook: Map[String, OrdersBook] = poloniex.ordersBook(20)

    ordersBook map { case (name, order) =>
      assertOrder(order)
    }

  }

  def assertOrder(order: OrdersBook): Unit = {
    assert(order.bids.nonEmpty)
    assert(order.asks.nonEmpty)

    order.asks map {
      case (price: OrderKey, volume: BigDecimal) => assert(price.toFloat > 0 && volume > 0)
      case _ => assert(false, "should't be here")
    }
  }
}
