package app.bitrader.api

import android.os.Build.VERSION_CODES._
import app.bitrader.api.poloniex.CurrencyPair
import org.robolectric.annotation.Config

/**
  * Created by Alex Afanasev
  */
@Config(sdk = Array(LOLLIPOP))
class OrdersBookSpec extends ApiSpec{
  it should "work with curr pairs" in {
    assert(CurrencyPair.BTC_ETH.toString == "BTC_ETH")
    assert(CurrencyPair.BTC_NXT.toString == "BTC_NXT")
  }
}
