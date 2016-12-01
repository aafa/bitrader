package app.bitrader

import app.bitrader.api.ApiProvider
import app.bitrader.api.common.CurrencyPair.CurrencyPair
import app.bitrader.api.common.{OrderWampMsg, WampMsg}
import app.bitrader.api.network.WampSub
import app.bitrader.api.poloniex.{Chart, OrdersBook}

/**
  * Created by Alex Afanasev
  */

trait MyAction extends diode.Action

case class SelectApi(api: ApiProvider) extends MyAction

case class UpdateOrderBook(cp: CurrencyPair) extends MyAction

case class ReceiveOrderBook(ob: OrdersBook) extends MyAction

case class AddWampMessage[T <: WampMsg](m: T) extends MyAction

case class AddWampMessages[T <: WampMsg](ms: Seq[T]) extends MyAction

case class ResetWampMessages[T <: WampMsg]() extends MyAction

case class SubscribeToOrders(t: WampSub[OrderWampMsg]) extends MyAction

case object CloseWampChannel extends MyAction

case class UpdateCurrencies(api: ApiProvider) extends MyAction

case class CurrenciesUpdated(api: ApiProvider) extends MyAction

case class UpdateCharts(cp: CurrencyPair) extends MyAction

case class ChartsUpdated(c: Seq[Chart]) extends MyAction

