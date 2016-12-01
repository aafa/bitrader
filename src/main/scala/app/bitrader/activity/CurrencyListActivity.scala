package app.bitrader.activity

import android.os.Bundle
import android.widget.{LinearLayout, TextView}
import app.bitrader.activity.menu.PairsListFragment
import app.bitrader.helpers.{Id, Styles}
import com.orhanobut.logger.Logger
import macroid.FullDsl._
import macroid._

/**
  * Created by Alex Afanasev
  */
class CurrencyListActivity extends BaseActivity with Styles{

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)

    setTitle("CurrencyListActivity")
    Logger.d("CurrencyListActivity !")

    val ui: Ui[LinearLayout] = l[LinearLayout](
      f[PairsListFragment].framed(Id.map, Tag.map) <~ vMatchParent
    ) <~ vMatchParent

    setContentView(ui.get)
  }
}
