package app.bitrader.activity.menu

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.widget.NestedScrollView
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{LinearLayout, TextView}
import app.bitrader.activity.BaseFragment
import app.bitrader.helpers.{Styles, TweaksAndGoodies}
import com.fortysevendeg.macroid.extras.CardViewTweaks._
import macroid.FullDsl._
import macroid.{ContextWrapper, Ui}

/**
  * Created by Alexey Afanasev on 15.02.16.
  */
class PairsListFragment extends BaseFragment with PairsListLayout {
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {

    ui
  }
}

trait PairsListLayout extends TweaksAndGoodies {

  def ui(implicit c: ContextWrapper) = {
    val p = 5.dp

    def card(children: Ui[View]*): Ui[W] = {
      l[W](
        children.map(_ <~ vMatchWidth): _*
      ) <~ vMatchWidth <~ cardTweak(p) <~ cvCardBackgroundColor(Color.LTGRAY)
    }

    scrollable(
      card(w[TextView] <~ text("test fragment2")),
      card(w[TextView] <~ text("test fragment2"))
    ) <~ padding(top = p)

  }.get
}