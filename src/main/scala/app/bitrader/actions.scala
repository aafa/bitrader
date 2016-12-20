package app.bitrader

import android.support.v4.app.Fragment
import app.bitrader.api.ApiProvider
import app.bitrader.api.common.CurrencyPair.CurrencyPair
import app.bitrader.api.common.{OrderWampMsg, WampMsg}
import app.bitrader.api.network.WampSub
import app.bitrader.api.poloniex.{Chart, Currency, OrdersBook}
import macroid.FragmentBuilder

/**
  * Created by Alex Afanasev
  */

trait Action extends diode.Action

case class SelectApi(api: Account) extends Action

case class UpdateOrderBook(cp: CurrencyPair) extends Action

case class ReceiveOrderBook(ob: OrdersBook) extends Action

case class AddWampMessage[T <: WampMsg](m: T) extends Action

case class AddWampMessages[T <: WampMsg](ms: Seq[T]) extends Action

case class ResetWampMessages[T <: WampMsg]() extends Action

case class SubscribeToOrders(t: WampSub[OrderWampMsg]) extends Action

case object CloseWampChannel extends Action

case object UpdateCurrencies extends Action

case class CurrenciesUpdated(cl: CurrenciesList) extends Action

case class UpdateCharts(cp: CurrencyPair) extends Action

case class ChartsUpdated(c: Seq[Chart]) extends Action

case class SetMainFragment(f: FragmentBuilder[_ <: Fragment]) extends Action

case object UpdateBalances extends Action

case class BalancesUpdated(bb: BalancesList) extends Action

