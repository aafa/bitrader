package app.bitrader.activity

import java.util.concurrent.TimeUnit

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.{LinearLayout, TextView}
import app.bitrader.{AddMessage, AppCircuit, Message}
import diode.{Dispatcher, Effect}
import diode.data.{Fetch, PotStream, StreamValue}
import macroid.{ContextWrapper, Contexts, Ui}
import rx.{Observer, Subscriber, Subscription}
import ws.wamp.jawampa.WampClient.{ConnectedState, State}
import ws.wamp.jawampa.{PubSubData, WampClient, WampClientBuilder}
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider
import macroid.FullDsl._
import macroid._
import io.github.aafa.helpers.Styles

import scala.util.Random

/**
  * Created by Alex Afanasev
  */
class WampActivity extends Activity with Contexts[Activity] {

  lazy val view = new WampView
  lazy val javampa = new JawampaClient(AppCircuit)
  lazy val subscription = AppCircuit.subscribe(AppCircuit.zoom(_.messages))(m => view.update(m.value.reverse.mkString))

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    javampa.connect()
    subscription
    setContentView(view.ui)
  }

  override def onPause(): Unit = {
    super.onPause()
    javampa.closeConnection()
    subscription.apply()
  }
}

class WampView(implicit c: ContextWrapper) extends Styles {
  var testText = slot[TextView]

  val ui: View = {
    w[TextView] <~ wire(testText) <~ vMatchParent
  }.get

  def update(s: String) = Ui.run(testText <~ text(s))

  def appendText(s: String) = Ui.run(testText <~ text((testText ~> getText) + s))
}

class JawampaClient(dispatcher: Dispatcher)(implicit ctx: ContextWrapper) {

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
    onConnected(() => {
      wampSubscription("BTC_ETH")
    }
    )
    wamp.open()
  }

  private def onConnected(subs: () => Unit): Subscription = {
    wamp.statusChanged().subscribe(new Observer[State] {
      override def onCompleted(): Unit = {
        println("statusChanged onCompleted")
      }

      override def onError(e: Throwable): Unit = println(s"statusChanged onError $e")

      override def onNext(t: State): Unit = t match {
        case c: ConnectedState =>
          println(s"next status $t")
          subs()
        case _ => println(s"next status $t")
      }
    })
  }

  private def wampSubscription(topic: String): PotStream[Long, String] = {
    val stream = PotStream[Long, String](new NoopFetch)
    wamp.makeSubscription(topic).subscribe(
      new Observer[PubSubData] {
        override def onCompleted(): Unit = println(s"$topic sub onCompleted")

        override def onError(e: Throwable): Unit = println(s"$topic sub onError $e")

        override def onNext(t: PubSubData): Unit = {
          dispatcher(AddMessage(Message(t.arguments().toString)))
        }
      })
    stream
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

class NoopFetch extends Fetch[Long] {
  override def fetch(key: Long): Unit = {}

  override def fetch(keys: Traversable[Long]): Unit = {}
}
