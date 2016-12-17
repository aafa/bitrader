package app.bitrader.activity.fragments
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import macroid.FullDsl._

/**
  * Created by Alex Afanasev
  */
class TestFragment extends BaseFragment{
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val ui = w[TextView] <~ text("test")
    ui.get
  }
}
