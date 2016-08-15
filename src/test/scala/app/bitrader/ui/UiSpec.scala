package app.bitrader.ui

import android.view.{Menu, View}
import android.widget.ListView
import app.bitrader.{AbstractSpec, R, TR}
import app.bitrader.activity.MainActivity
import com.github.mikephil.charting.charts.CandleStickChart
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

  }

  it should "have suggestionList reacting to input properly" in {
    mainActivity.searchView.performClick()
    mainActivity.searchView.setQuery("BTC", false)

    val suggestionList: ListView = mainActivity.searchView.findViewById(R.id.suggestion_list).asInstanceOf[ListView]

    assert(suggestionList.getAdapter.getCount > 0)
    assertThat(suggestionList).hasCount(3)

    mainActivity.searchView.setQuery("BTC_NONE", false)
    assertThat(suggestionList).hasCount(0)
  }

  it should "have chart loaded" in {
    val chart: CandleStickChart = mainActivity.layout.candleStick.get
    assertThat(chart).isVisible

    assert(chart.getCandleData != null)
    assert(chart.getCandleData.getDataSetCount > 0)
    assert(chart.getCandleData.getDataSets.get(0).getLabel == "Data")
    assert(chart.getCandleData.getDataSets.get(0).getEntryCount > 0)
  }
}
