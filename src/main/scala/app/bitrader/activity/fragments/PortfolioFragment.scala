package app.bitrader.activity.fragments
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{LinearLayout, ListView, TextView}
import app.bitrader._
import app.bitrader.activity.CurrencyDetailsActivity
import app.bitrader.{CurrenciesList, ICircuit, TR, startActivityWithParams}
import app.bitrader.activity.fragments.BalanceListable.card
import app.bitrader.activity.layouts.ListLayout
import app.bitrader.api.poloniex.Currency
import app.bitrader.helpers.TweaksAndGoodies
import macroid.{ContextWrapper, Ui}
import macroid.FullDsl.{wire, _}
import macroid._
import macroid.contrib.ListTweaks
import macroid.viewable.SlottedListable

/**
  * Created by Alex Afanasev
  */
class PortfolioFragment extends BaseFragment{

  lazy val layout = new PortfolioLayout

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    appCircuit.dataSubscribe(_.balance)(layout.updateData)
    appCircuit(UpdateBalances)
    layout.ui
  }
}

class PortfolioLayout(implicit cw: ContextWrapper) extends ListLayout {
  private var listView = slot[ListView]

  def updateData(a: Option[BalancesList]) = {
    def go(p: Currency): Unit = startActivityWithParams[CurrencyDetailsActivity](p)
    val adapterTweak = a map (c => BalanceListable.listAdapterTweak(c.toSeq))

    Ui.run(listView <~ adapterTweak <~ adapterOnClick(go))
  }

  def ui = {
    w[ListView] <~ wire(listView) <~ vMatchParent <~ ListTweaks.noDivider <~
      padding(top = TR.dimen.default_offset.get)
  }.get

}

object BalanceListable extends SlottedListable[Balance] with TweaksAndGoodies {

  class Slots {
    var name = slot[TextView]
    var value = slot[TextView]
  }

  def makeSlots(viewType: Int)(implicit ctx: ContextWrapper): (Ui[View], Slots) = {
    val slots = new Slots
    val view =
      l[LinearLayout](
        card(
          l[LinearLayout](
            w[TextView] <~ wire(slots.name),
            w[TextView] <~ wire(slots.value)
          ) <~ vertical
        ))
    (view, slots)
  }


  def fillSlots(slots: Slots, data: Balance)(implicit ctx: ContextWrapper): Ui[Any] = {
    (slots.name <~ text(data._1)) ~
      (slots.value <~ text(data._2))
  }
}