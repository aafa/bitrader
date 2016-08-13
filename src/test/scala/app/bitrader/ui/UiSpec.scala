package app.bitrader.ui

import android.view.Menu
import app.bitrader.{AbstractSpec, R, TR}
import app.bitrader.activity.MainActivity
import org.assertj.android.api.Assertions._
import org.robolectric.fakes.RoboMenuItem
import org.robolectric.shadows.ShadowFrameLayout
import org.robolectric.{Robolectric, Shadows}

/**
  * Created by Alex Afanasev
  */
class UiSpec extends AbstractSpec {
  lazy val mainActivity: MainActivity = Robolectric.setupActivity(classOf[MainActivity])

  it should "have search menu" in {
    val menu: Menu = Shadows.shadowOf(mainActivity).getOptionsMenu
    assertThat(menu).hasSize(1)
    assertThat(menu).hasItem(R.id.action_search)
  }

  it should "start activity" in {
    assertThat(mainActivity).hasTitle("Poloniex")

    assertThat(mainActivity.searchView).isNotNull
    assertThat(mainActivity.searchView).isVisible

    mainActivity.searchView.performClick()
    mainActivity.searchView.setQuery("BTC_ETH", false)

    val layout: ShadowFrameLayout = Shadows.shadowOf(mainActivity.searchView)

  }
}
