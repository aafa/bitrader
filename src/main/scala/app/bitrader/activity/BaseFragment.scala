package app.bitrader.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import macroid.Contexts

/**
  * Created by Alex Afanasev
  */
class BaseFragment extends Fragment with Contexts[Fragment] with Circuitable{
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View =
    super.onCreateView(inflater, container, savedInstanceState)
}