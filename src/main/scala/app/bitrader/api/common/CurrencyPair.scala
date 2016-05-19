package app.bitrader.api.common

import app.ObjectEnum

/**
  * Created by Alex Afanasev
  */
sealed trait CurrencyPair extends CurrencyPair.Value

object CurrencyPair extends ObjectEnum[CurrencyPair] {
  case object BTC_ETH extends CurrencyPair
  case object BTC_NXT extends CurrencyPair
  case object BTC_XMR extends CurrencyPair
}