package app.bitrader.unit

import android.os.Build.VERSION_CODES.LOLLIPOP
import app.bitrader.{AbstractSpec, LocalProperties, R}
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Config(sdk = Array(LOLLIPOP))
class LocalPropsSpec extends AbstractSpec  {

  "LocalProperties" should "work" in {
    assert(LocalProperties.prop.getProperty("testKey") == "ok")
  }

}