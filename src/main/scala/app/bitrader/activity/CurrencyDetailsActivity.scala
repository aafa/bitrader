package app.bitrader.activity

import android.os.Bundle
import android.widget.{LinearLayout, TextView}
import app.bitrader.activity.layouts.BasicLayout
import app.bitrader.api.poloniex.Currency
import macroid.ContextWrapper
import macroid.FullDsl._

/**
  * Created by Alex Afanasev
  */
class CurrencyDetailsActivity extends BaseActivity {
  private val maybeCurrency = getParam[Currency]
  private lazy val layout = new CurrencyDetailsLayout(maybeCurrency)

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    setContentView(layout.ui)
  }
}

class CurrencyDetailsLayout(maybeCurrency: Option[Currency])(implicit cw: ContextWrapper) extends BasicLayout {
  def ui = l[LinearLayout](
    w[TextView] <~ (maybeCurrency map (c => text(c.name)))
  ).get
}
