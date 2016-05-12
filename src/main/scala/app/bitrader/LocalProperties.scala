package app.bitrader

import java.io.FileInputStream
import java.util.Properties

/**
  * Created by Alex Afanasev
  */
object LocalProperties {

  val prop = {
    val p = new Properties()
    p.load(new FileInputStream("local.properties"))
    p
  }

  val apiKey = prop.getProperty("apiKey")
  val apiSecret = prop.getProperty("apiSecret")

}
