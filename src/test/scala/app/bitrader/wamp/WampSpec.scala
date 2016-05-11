package app.bitrader.wamp

import app.bitrader.AbstractSpec
import app.bitrader.activity.{JawampaClient, MessagesAdapter}

/**
  * Created by Alex Afanasev
  */
class WampSpec extends AbstractSpec{
  class TestMessages extends MessagesAdapter

  def bd(k: Int, v: Int): (BigDecimal, BigDecimal) = (BigDecimal(k), BigDecimal(v))

  it should "work with wamp" in {
    val messages = new TestMessages
    messages.updateOrderList(Seq(bd(1,5), bd(2,3)))

    assert(messages.orders.size == 2)

    messages.removeOrder(BigDecimal(1))

    assert(messages.orders.size == 1)
    assert(messages.orders.head._1 == 2)
    assert(messages.orders.head._2 == 3)

    messages.updateOrder(bd(2,5))
    assert(messages.orders.head._1 == 2)
    assert(messages.orders.head._2 == 5)
  }
}
