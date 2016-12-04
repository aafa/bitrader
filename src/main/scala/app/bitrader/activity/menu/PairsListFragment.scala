package app.bitrader.activity.menu

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{LinearLayout, ListView, TextView}
import app.bitrader.activity.BaseFragment
import app.bitrader.api.poloniex.Currency
import app.bitrader.helpers.TweaksAndGoodies
import app.bitrader.{ICircuit, UpdateCurrencies, _}
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import macroid.FullDsl.{text, _}
import macroid.viewable.Listable
import macroid.{ContextWrapper, Ui, _}

/**
  * Created by Alexey Afanasev on 15.02.16.
  */
class PairsListFragment extends BaseFragment {
  private lazy val layout = new PairsListLayout(appCircuit)
  appCircuit.dataSubscribe(_.currencies)(layout.updateData)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    appCircuit(UpdateCurrencies)
    layout.ui
  }
}

class PairsListLayout(appCircuit: ICircuit)(implicit cw: ContextWrapper) extends TweaksAndGoodies with PagingListable {
  private var content = slot[LinearLayout]
  private var listView = slot[ListView]
  private val p = 5.dp

  def updateData(a: CurrenciesList) = {
    def tv(t: String) = card(w[TextView] <~ text(t))
    def views: Seq[Ui[View]] = a.map { case (s, _) => tv(s) }.toSeq
    def go(p: Currency): Unit = Ui.run(toast(p.name) <~ fry)

    Ui.run(listView <~ ccListable.listAdapterTweak(a.values.toSeq) <~ adapterOnClick(go))
  }

  def ui = {
    l[CoordinatorLayout](
      w[ListView] <~ wire(listView)
    ) <~ vMatchParent <~ padding(top = p)
  }.get

  def card(children: Ui[View]*): Ui[View] = l[W](
    children.map(_ <~ vMatchWidth): _*
  ) <~ vMatchWidth <~ cardTweak(p) <~ cvCardBackgroundColor(Color.LTGRAY)

  def ccListable: Listable[Currency, TextView] = Listable[Currency] {
    w[TextView]
  } { view ⇒
    currency ⇒
      view <~ text(currency.name)
  }
}