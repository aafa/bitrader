package app.bitrader.activity.menu

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import io.github.aafa.helpers.Styles
import macroid.FullDsl._
import macroid.{ContextWrapper, Contexts}


/**
  * Created by Alexey Afanasev on 15.02.16.
  */
class UserDetailsFragment extends Fragment with Contexts[Fragment] with UserDetailsLayout{
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = ui
}

trait UserDetailsLayout extends Styles{
  def ui(implicit c: ContextWrapper) = {
    l[CoordinatorLayout](

    ) <~ vMatchParent <~ padding(all = 20.dp)

  }.get
}