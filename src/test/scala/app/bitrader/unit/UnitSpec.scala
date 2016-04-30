package app.bitrader.unit

import android.os.Build.VERSION_CODES.LOLLIPOP
import app.bitrader.{AbstractSpec, R}
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.scalatest.{FlatSpec, Matchers, RobolectricSuite}

@Config(sdk = Array(LOLLIPOP))
class UnitSpec extends AbstractSpec  {

  "Resources" should "be accessible via R" in {
    RuntimeEnvironment.application.getString(R.string.app_name) shouldBe "bitrader"
  }

}