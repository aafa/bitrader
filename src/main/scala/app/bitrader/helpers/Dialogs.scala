package app.bitrader.helpers

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import app.bitrader.helpers.UiThreading
import macroid.FullDsl._
import macroid.{ContextWrapper, Ui}

/**
  * Created by Alexey Afanasev on 23.03.16.
  */
trait Dialogs extends UiThreading{
  protected[this] implicit def cw: ContextWrapper

  def showToast(t: String) = Ui.run(toast(t)(cw) <~ fry)

  def showLongToast(t: String) = Ui.run(toast(t)(cw) <~ long <~ fry)

  def spinner(t: String): Unit = spinnerDialog(t, null)

  def spinner(): Unit = spinner("Подождите...")

  @inline def spinnerDialog(title: CharSequence, message: CharSequence) =
    runOnUiThread(ProgressDialog.show(cw.bestAvailable, title, message, true))

  // alerts

  def action(doAction: => Unit): DialogInterface.OnClickListener = {
    new OnClickListener {
      override def onClick(dialog: DialogInterface, which: Int): Unit = doAction
    }
  }

  @inline def confirm(t: String, yes: => Unit, no: => Unit = () => ()) = {
    Ui.run {
      dialog(t) <~
        positiveYes(action(yes)) <~
        negativeNo(action(no)) <~
        speak
    }
  }

  @inline def alert(t: String, d: String, yes: => Unit = () => ()) = {
    Ui.run {
      dialog(t + " " + d) <~
        positiveYes(action(yes)) <~
        speak
    }
  }

}
