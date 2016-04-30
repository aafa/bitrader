package app.bitrader.unit

import app.bitrader.api.ApiSpec
import app.bitrader.api.poloniex.CurrencyPair

/**
  * Created by Alex Afanasev
  */
class CurrencySpec extends UnitSpec{

  "CurrencyPair" should "work have string representation" in {
    assert(CurrencyPair.BTC_ETH.toString == "BTC_ETH")
    assert(CurrencyPair.BTC_NXT.toString == "BTC_NXT")
  }

  "CurrencyPair" should "have values list" in {
    CurrencyPair.BTC_XMR // its awkward that we need to call them first
    CurrencyPair.BTC_ETH

    assert(CurrencyPair.values contains CurrencyPair.BTC_ETH)
    assert(CurrencyPair.values contains CurrencyPair.BTC_XMR)
  }

}
