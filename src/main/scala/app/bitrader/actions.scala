package app.bitrader

import app.bitrader.api.ApiProvider
import app.bitrader.api.common.CurrencyPair.CurrencyPair
import app.bitrader.api.common.{OrderWampMsg, WampMsg}
import app.bitrader.api.network.WampSub
import app.bitrader.api.poloniex.{Chart, OrdersBook}

/**
  * Created by Alex Afanasev
  */

case class SelectApi(api: ApiProvider)

case class UpdateOrderBook(cp: CurrencyPair)

case class ReceiveOrderBook(ob: OrdersBook)

case class AddWampMessage[T <: WampMsg](m: T)

case class AddWampMessages[T <: WampMsg](ms: Seq[T])

case class ResetWampMessages[T <: WampMsg]()

case class SubscribeToOrders(t: WampSub[OrderWampMsg])

case object CloseWampChannel

case class UpdateCurrencies(api: ApiProvider)

case class CurrenciesUpdated(api: ApiProvider)

case class UpdateCharts(cp: CurrencyPair)

case class ChartsUpdated(c: Seq[Chart])

