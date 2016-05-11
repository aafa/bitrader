package app.bitrader.wamp

import app.bitrader._
import app.bitrader.activity.{JawampaClient, MessagesAdapter}

import scala.collection.SortedMap

/**
  * Created by Alex Afanasev
  */
class WampSpec extends AbstractSpec{
  class TestMessages extends MessagesAdapter

  def bd(k: Int, v: Int): (OrderKey, OrderValue) = (BigDecimal(k), BigDecimal(v))

  it should "work with wamp" in {
    val messages = new TestMessages

    def orders = messages.orders

    def get(key: Int): (Any, Any) = {
      orders.find { case (k, v) => k == key }.get
    }

    messages.updateOrderList(SortedMap(bd(1,5), bd(5,1), bd(2,3)))

    assert(orders.size == 3)
    assert(orders.keys.toSeq == Seq(1,2,5))
    assert(orders.values.toSeq == Seq(5,3,1))

    messages.removeOrder(1.emptyPair)

    assert(orders.size == 2)
    assert(orders.head == (2,3))

    messages.updateOrder(bd(2,10))
    assert(orders.size == 2)
    assert(orders.head == (2, 10))

    messages.updateOrder(bd(5,25))
    assert(orders.size == 2)
    assert(get(5) == (5,25))

    messages.addOrder(4,15)
    assert(orders.size == 3)
    assert(get(4) == (4,15))

  }
}
