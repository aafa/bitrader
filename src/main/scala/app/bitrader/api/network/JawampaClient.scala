package app.bitrader.api.network

import java.util.concurrent.TimeUnit

import app.bitrader.{APIContext, AddWampMessages, WampMsg}
import diode.Dispatcher
import diode.data.{Fetch, PotStream}
import macroid.ContextWrapper
import rx.{Observer, Subscription}
import ws.wamp.jawampa.WampClient.{ConnectedState, State}
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider
import ws.wamp.jawampa.{PubSubData, WampClient, WampClientBuilder}

/**
  * Created by Alex Afanasev
  */
class JawampaClient(dispatcher: Dispatcher)(implicit ctx: ContextWrapper) {
  private val wsuri = "wss://api.poloniex.com" // todo from TR

  class TickerData(currencyPair: String, last: BigDecimal, lowestAsk: BigDecimal, highestBid: BigDecimal,
                   percentChange: BigDecimal, baseVolume: BigDecimal, quoteVolume: BigDecimal, isFrozen: Byte,
                   dayHigh: BigDecimal, dayLow: BigDecimal) {
    override def toString: String = s"Got $currencyPair for $last"
  }

  private val wamp: WampClient = buildWamp()

  def closeConnection() = {
    wamp.close()
  }

  def openSubscription[WM <: WampMsg : scala.reflect.Manifest](subs: WampSub[WM]*) = {
    onConnected(subs.seq.foreach { s =>
      wampSubscription[s.WampSubType](s.topic)
    })
    wamp.open()
  }

  def addSubscription[WM <: WampMsg : scala.reflect.Manifest](subs: WampSub[WM]*) = ???

  private def wampSubscription[WM <: WampMsg : scala.reflect.Manifest](topic: String): PotStream[Long, String] = {
    val stream = PotStream[Long, String](new NoopFetch) // todo stream?
    wamp.makeSubscription(topic).subscribe(
      new Observer[PubSubData] {
        override def onCompleted(): Unit = println(s"$topic sub onCompleted")

        override def onError(e: Throwable): Unit = println(s"$topic sub onError $e")

        override def onNext(t: PubSubData): Unit = {
          val value: Seq[WM] = APIContext.jacksonMapper.readValue[Seq[WM]](t.arguments().toString)
          dispatcher(AddWampMessages[WM](value))
        }
      })
    stream
  }

  private def onConnected(subs: => Unit): Subscription = {
    wamp.statusChanged().subscribe(new Observer[State] {
      override def onCompleted(): Unit = {
        println("statusChanged onCompleted")
      }

      override def onError(e: Throwable): Unit = println(s"statusChanged onError $e")

      override def onNext(t: State): Unit = t match {
        case c: ConnectedState =>
          println(s"next status $t")
          subs
        case _ => println(s"next status $t")
      }
    })
  }

  private def buildWamp(): WampClient = {
    new WampClientBuilder()
      .withConnectorProvider(new NettyWampClientConnectorProvider)
      .withUri(wsuri)
      .withRealm("realm1")
      .withInfiniteReconnects()
      .withReconnectInterval(5, TimeUnit.SECONDS)
      .build()
  }

  private class NoopFetch extends Fetch[Long] {
    override def fetch(key: Long): Unit = {}

    override def fetch(keys: Traversable[Long]): Unit = {}
  }

}

case class WampSub[T <: WampMsg : scala.reflect.Manifest](topic: String) {
  type WampSubType = T
}



