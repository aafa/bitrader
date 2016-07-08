package app.bitrader.api

import app.bitrader.api.common.CurrencyPair
import app.bitrader.api.poloniex.{ActualOrder, CompleteBalance, OrderDetails}

/**
  * Created by Alex Afanasev
  */
class BalancesSpec extends ApiSpec {

  it should "check balance" in {
    val balances: Map[String, CompleteBalance] = poloniex.returnCompleteBalances

    println("total btc amount " + balances.map(_._2.btcValue.toDouble).sum)

    println(poloniex.returnDepositAddresses)
  }

}
