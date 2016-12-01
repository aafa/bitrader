package app.bitrader.activity.menu

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import app.bitrader.activity.BaseFragment
import app.bitrader.helpers.Styles
import macroid.ContextWrapper
import macroid.FullDsl._


/**
  * Created by Alexey Afanasev on 15.02.16.
  */
class PairsListFragment extends BaseFragment with PairsListLayout {
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = ui
}

trait PairsListLayout extends Styles {
  def ui(implicit c: ContextWrapper): CoordinatorLayout = {
    l[CoordinatorLayout](
      w[TextView] <~ text("test fragment")
    ) <~ vMatchParent <~ padding(all = 20.dp)

  }.get
}