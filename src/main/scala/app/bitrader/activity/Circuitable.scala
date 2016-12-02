package app.bitrader.activity

import app.bitrader.{AppContext, ICircuit}

/**
  * Created by Alex Afanasev
  */
trait Circuitable {
  val appCircuit: ICircuit = AppContext.diModule.appCircuit
}
