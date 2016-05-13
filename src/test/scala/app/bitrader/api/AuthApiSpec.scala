package app.bitrader.api

import java.util.Date

/**
  * Created by Alex Afanasev
  */
class AuthApiSpec extends ApiSpec {


  it should "do auth" in {
    val nonce = new Date().getTime.toString

    val balances: Map[String, String] = poloniexPrivateApi.balances(nonce, "returnBalances")
    println(s"balances $balances")
    assert(balances.nonEmpty)
    assert(balances.head._1 != "error")


  }
}
