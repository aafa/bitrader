package app.bitrader.activity.menu

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{LinearLayout, ListView, TextView}
import app.bitrader.activity.BaseFragment
import app.bitrader.activity.layouts.ListLayout
import app.bitrader.api.poloniex.Currency
import app.bitrader.helpers.TweaksAndGoodies
import app.bitrader.{ICircuit, UpdateCurrencies, _}
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import macroid.FullDsl.{text, _}
import macroid.viewable.{Listable, SlottedListable}
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
  private var content = slot[LinearLayout]
  private var listView = slot[ListView]
  private val p = 5.dp

  def updateData(a: CurrenciesList) = {
    def tv(t: String) = card(w[TextView] <~ text(t))
    def views: Seq[Ui[View]] = a.map { case (s, _) => tv(s) }.toSeq
    def go(p: Currency): Unit = showToast(p.name)

    Ui.run(listView <~ CurrencyListable.listAdapterTweak(a.values.toSeq) <~ adapterOnClick(go))
  }

  def ui = {
    l[CoordinatorLayout](
      w[ListView] <~ wire(listView) <~ vMatchParent
    ) <~ vMatchParent <~ padding(top = p)
  }.get

  def card(children: Ui[View]*): Ui[View] = l[W](
    children.map(_ <~ vMatchWidth): _*
  ) <~ vMatchWidth <~ cardTweak(p) <~ cvCardBackgroundColor(Color.LTGRAY)

}

object CurrencyListable extends SlottedListable[Currency] {
  class Slots {
    var name = slot[TextView]
    var value = slot[TextView]
  }

  def makeSlots(viewType: Int)(implicit ctx: ContextWrapper): (Ui[View], Slots) = {
    val slots = new Slots
    val view = l[LinearLayout](
      w[TextView] <~ wire(slots.name),
      w[TextView] <~ wire(slots.value)
    ) <~ vertical <~ vMatchParent
    (view, slots)
  }

   def fillSlots(slots: Slots, data: Currency)(implicit ctx: ContextWrapper): Ui[Any]= {
    (slots.name <~ text(data.name)) ~
      (slots.value <~ text(data.txFee.toString()))
  }
}