package app.bitrader.activity

import app.bitrader.AppContext

/**
  * Created by Alex Afanasev
  */
trait Circuitable {
  val appCircuit = AppContext.diModule.appCircuit
}
