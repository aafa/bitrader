package app.bitrader.activity.menu

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import app.bitrader.helpers.Styles
import macroid.FullDsl._
import macroid.{ContextWrapper, Contexts}


/**
  * Created by Alexey Afanasev on 15.02.16.
  */
class PairsListFragment extends Fragment with Contexts[Fragment] with PairsListLayout{
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = ui
}

trait PairsListLayout extends Styles{
  def ui(implicit c: ContextWrapper) = {
    l[CoordinatorLayout](

    ) <~ vMatchParent <~ padding(all = 20.dp)

  }.get
}