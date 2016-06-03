package app.bitrader.api.common

import app.ObjectEnum

/**
  * Created by Alex Afanasev
  */

object CurrencyPair extends Enumeration {
  type CurrencyPair = Value
  val BTC_ETH = Value("BTC_ETH")
  val BTC_NXT = Value("BTC_NXT")
  val BTC_XMR = Value("BTC_XMR")
}

