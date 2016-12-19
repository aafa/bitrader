package app.bitrader

import java.io.FileInputStream
import java.util.Properties

/**
  * Created by Alex Afanasev
  */
object LocalProperties {

  val prop = {
    val p = new Properties()
    val resourceAsStream = this.getClass.getClassLoader.getResourceAsStream("local.properties")
    p.load(resourceAsStream)
    p
  }

  val apiKey = prop.getProperty("apiKey")
  val apiSecret = prop.getProperty("apiSecret")

}
