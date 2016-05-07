package app.bitrader.wamp

import app.bitrader.AbstractSpec
import app.bitrader.activity.{JawampaClient}

/**
  * Created by Alex Afanasev
  */
class WampSpec extends AbstractSpec{
  class TestWamp extends JawampaClient

  it should "work with wamp" in {
    val wamp: TestWamp = new TestWamp
    wamp.connect()
  }
}
