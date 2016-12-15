package app.bitrader.activity.menu

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{LinearLayout, ListView, TextView}
import app.bitrader.activity.layouts.ListLayout
import app.bitrader.activity.{BaseFragment, CurrencyDetailsActivity}
import app.bitrader.api.poloniex.Currency
import app.bitrader.helpers.TweaksAndGoodies
import app.bitrader.{ICircuit, UpdateCurrencies, _}
import macroid.FullDsl.{text, _}
import macroid.contrib.ListTweaks
import macroid.viewable.SlottedListable
import macroid.{ContextWrapper, Ui, _}

/**
  * Created by Alexey Afanasev
  */

class PairsListFragment extends BaseFragment {
  private lazy val layout = new PairsListLayout(appCircuit)
  appCircuit.dataSubscribe(_.currencies)(layout.updateData)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    appCircuit(UpdateCurrencies)
    layout.ui
  }
}

class PairsListLayout(appCircuit: ICircuit)(implicit cw: ContextWrapper) extends ListLayout {
  private var listView = slot[ListView]

  def updateData(a: CurrenciesList) = {
    def go(p: Currency): Unit = startActivityWithParams[CurrencyDetailsActivity](p)

    Ui.run(listView <~ CurrencyListable.listAdapterTweak(a.values.toSeq) <~ adapterOnClick(go))
  }

  def ui = {
    l[CoordinatorLayout](
      w[ListView] <~ wire(listView) <~ vMatchParent <~ ListTweaks.noDivider
    ) <~ vMatchParent <~ padding(top = TR.dimen.default_offset.get)
  }.get

}

object CurrencyListable extends SlottedListable[Currency] with TweaksAndGoodies {

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


  def fillSlots(slots: Slots, data: Currency)(implicit ctx: ContextWrapper): Ui[Any] = {
    (slots.name <~ text(data.name)) ~
      (slots.value <~ text(data.txFee.toString()))
  }
}