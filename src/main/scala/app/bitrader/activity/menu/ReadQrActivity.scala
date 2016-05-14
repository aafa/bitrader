package app.bitrader.activity.menu

import android.app.Activity
import android.graphics.PointF
import android.os.Bundle
import android.widget.{Button, LinearLayout, TextView}
import app.bitrader.activity.MainStyles
import com.google.zxing.Result
import macroid.{Contexts, Ui}
import macroid.FullDsl._
import macroid._
import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
  * Created by Alex Afanasev
  */
class ReadQrActivity extends Activity with Contexts[Activity] with ZXingScannerView.ResultHandler with MainStyles {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(ui)
  }

  var qrText = slot[TextView]

  def ui = {
    l[LinearLayout](
      w[TextView] <~ wire(qrText) <~ vWrap,
      w[Button] <~ text("read qr") <~ onClick(runQr) <~ vWrapContent
    ) <~ vMatchWidth
  }.get

  lazy val scannerView: ZXingScannerView = new ZXingScannerView(this)

  def runQr: Unit = {
    setContentView(scannerView)
    scannerView.setResultHandler(this)
    scannerView.startCamera()
  }

  override def onPause(): Unit = {
    super.onPause()
    scannerView.stopCamera()
  }

  override def handleResult(result: Result): Unit = {
    setContentView(ui)
    println(s"got qr! ${result.getText}")
    Ui.run(qrText <~ text(result.getText))

    scannerView.stopCamera()
  }


}

