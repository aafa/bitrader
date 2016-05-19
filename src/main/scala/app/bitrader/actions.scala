package app.bitrader

import app.bitrader.api.ApiService
import app.bitrader.api.common.{CurrencyPair, WampMsg}
import app.bitrader.api.poloniex.{Chart, OrdersBook}

/**
  * Created by Alex Afanasev
  */

case class UpdateOrderBook(cp: CurrencyPair)

case class ReceiveOrderBook(ob: OrdersBook)

case class AddWampMessage[T <: WampMsg](m: T)

case class AddWampMessages[T <: WampMsg](ms: Seq[T])

case class ResetWampMessages[T <: WampMsg]()

case class SubscribeToChannel(t: String)

case class UnsubscribeFromChannel(t: String)

case class UpdateCurrencies(api: ApiService)

case class CurrenciesUpdated(api: ApiService)

case object UpdateCharts

case class ChartsUpdated(c: Seq[Chart])

