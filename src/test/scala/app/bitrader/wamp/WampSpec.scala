package app.bitrader.wamp

import app.bitrader.AbstractSpec
import app.bitrader.activity.{JawampaWampTrait}

/**
  * Created by Alex Afanasev
  */
class WampSpec extends AbstractSpec{
  class TestWamp extends JawampaWampTrait

  it should "work with wamp" in {
    val wamp: TestWamp = new TestWamp
    wamp.connect()
  }
}
