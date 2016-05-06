package app.bitrader.activity

import java.util.concurrent.TimeUnit

import android.app.Activity
import android.os.Bundle
import rx.{Observer, Subscriber}
import ws.wamp.jawampa.WampClient.{ConnectedState, State}
import ws.wamp.jawampa.{PubSubData, WampClient, WampClientBuilder}
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider

/**
  * Created by Alex Afanasev
  */
class WampActivity extends Activity with JawampaWampTrait {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    connect()
  }

  override def onPause(): Unit = {
    super.onPause()
    closeConnection()
  }
}


trait JawampaWampTrait {

  val wsuri = "wss://api.poloniex.com"

  class TickerData(currencyPair: String, last: BigDecimal, lowestAsk: BigDecimal, highestBid: BigDecimal,
                   percentChange: BigDecimal, baseVolume: BigDecimal, quoteVolume: BigDecimal, isFrozen: Byte,
                   dayHigh: BigDecimal, dayLow: BigDecimal) {
    override def toString: String = s"Got $currencyPair for $last"
  }

  val wamp: WampClient = buildWamp()

  def closeConnection() = {
    wamp.close()
  }

  def connect() = {
    wamp.statusChanged().subscribe(new Observer[State] {
      override def onCompleted(): Unit = {
        println("statusChanged onCompleted")
      }

      override def onError(e: Throwable): Unit = println(s"statusChanged onError $e")

      override def onNext(t: State): Unit = t match {
        case c: ConnectedState =>
          println(s"next status $t")
          wampSubscription("ticker")
          wampSubscription("BTC_ETH")
        case _ => println(s"next status $t")
      }
    })

    wamp.open()
  }

  def wampSubscription(topic: String): Unit = {
    wamp.makeSubscription(topic).subscribe(
      new Observer[PubSubData] {
        override def onCompleted(): Unit = println("$topic sub onCompleted")

        override def onError(e: Throwable): Unit = println(s"$topic sub onError $e")

        override def onNext(t: PubSubData): Unit = println(s"$topic sub data ${t.arguments()}")
      })
  }

  def buildWamp(): WampClient = {
    new WampClientBuilder()
      .withConnectorProvider(new NettyWampClientConnectorProvider)
      .withUri(wsuri)
      .withRealm("realm1")
      .withInfiniteReconnects()
      .withReconnectInterval(5, TimeUnit.SECONDS)
      .build()
  }
}
