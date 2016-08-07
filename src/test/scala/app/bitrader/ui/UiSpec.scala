package app.bitrader.ui

import app.bitrader.AbstractSpec
import app.bitrader.activity.MainActivity
import org.robolectric.Robolectric
import org.assertj.android.api.Assertions._

/**
  * Created by Alex Afanasev
  */
class UiSpec extends AbstractSpec{
  it should "start activity" in {
    val upActivity: MainActivity = Robolectric.setupActivity(classOf[MainActivity])
    assertThat(upActivity).hasTitle("Poloniex")

  }
}
