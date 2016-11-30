package app.bitrader.activity

import android.os.Bundle
import android.widget.{LinearLayout, TextView}
import com.orhanobut.logger.Logger
import macroid.FullDsl._
import macroid._

/**
  * Created by Alex Afanasev
  */
class CurrencyListActivity extends BaseActivity{

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)

    setTitle("CurrencyListActivity")
    Logger.d("CurrencyListActivity !")

    val ui: Ui[LinearLayout] = l[LinearLayout](
      w[TextView] <~ text("test")
    )

    setContentView(ui.get)
  }
}
