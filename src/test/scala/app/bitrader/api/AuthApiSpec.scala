package app.bitrader.api

import scala.collection.immutable.Iterable

/**
  * Created by Alex Afanasev
  */
class AuthApiSpec extends ApiSpec {


  it should "do auth" in {

    val balances: Map[String, String] = poloniex.balances
    assert(balances.nonEmpty)
    assert(balances.head._1 != "error")

    val money = balances.filter {
      case (coin, amout) => amout.toDouble > 0
    }

    assert(money.nonEmpty)
    println(money)
  }
}
