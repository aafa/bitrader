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
    messages.updateOrderList(SortedMap(bd(1,5), bd(5,1), bd(2,3)))

    assert(messages.orders.size == 3)
    assert(messages.orders.keys.toSeq == Seq(1,2,5))
    assert(messages.orders.values.toSeq == Seq(5,3,1))

    messages.removeOrder(1)

    assert(messages.orders.size == 2)
    assert(messages.orders.head._1 == 2)
    assert(messages.orders.head._2 == 3)

    messages.updateOrder(bd(2,10))
    assert(messages.orders.head._1 == 2)
    assert(messages.orders.head._2 == 10)
  }
}
