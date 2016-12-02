package app.bitrader.activity.menu

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.widget.NestedScrollView
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{LinearLayout, TextView}
import app.bitrader.{ICircuit, UpdateCurrencies}
import app.bitrader.activity.BaseFragment
import app.bitrader.api.poloniex.Currency
import app.bitrader.helpers.{Styles, TweaksAndGoodies}
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import macroid.FullDsl._
import macroid.{ContextWrapper, Ui}

import scala.collection.immutable.Iterable

/**
  * Created by Alexey Afanasev on 15.02.16.
  */
class PairsListFragment extends BaseFragment  {
  private lazy val layout = new PairsListLayout(appCircuit)
  appCircuit.dataSubscribe(_.currencies)(layout.updateData)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    appCircuit(UpdateCurrencies)
    layout.ui
  }
}

class PairsListLayout(appCircuit : ICircuit) extends TweaksAndGoodies {
  def updateData(a: Map[String, Currency]) = {

  }


  def ui(implicit cw: ContextWrapper) = {
    val p = 5.dp

    def cards: Seq[Ui[View]] = {
      val zoom = appCircuit.serviceData.zoom(_.currencies).value
      zoom.map(c =>
        card(w[TextView] <~ text(c._1))
      ).toSeq
    }

    def card(children: Ui[View]*): Ui[W] = {
      l[W](
        children.map(_ <~ vMatchWidth): _*
      ) <~ vMatchWidth <~ cardTweak(p) <~ cvCardBackgroundColor(Color.LTGRAY)
    }

    scrollable(
      cards:_*
    ) <~ padding(top = p)

  }.get
}