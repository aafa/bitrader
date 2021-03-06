package app.bitrader.activity.menu

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import app.bitrader.activity.Circuitable
import app.bitrader.api.apitest.ApiTest
import app.bitrader.api.common.UserProfile
import app.bitrader.{AppCircuit, RootModel}
import diode.ModelR
import macroid.{ContextWrapper, Contexts}
import macroid.FullDsl._

/**
  * Created by Alex Afanasev
  */
class ProfileActivity extends Activity with Contexts[Activity] with Circuitable{
  private lazy val view: ProfileView = new ProfileView(appCircuit.serviceContext.zoom(_.auth))

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(view.ui)
  }
}

class ProfileView(m: ModelR[RootModel, UserProfile])(implicit cw: ContextWrapper) {
  def ui = {
    w[TextView] <~ text(m.value.authData.get.apiKey)
  }.get
}