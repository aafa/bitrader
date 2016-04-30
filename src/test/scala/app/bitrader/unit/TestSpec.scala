package app.bitrader.unit

import android.os.Build.VERSION_CODES.LOLLIPOP
import app.bitrader.R
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.scalatest.{FlatSpec, Matchers, RobolectricSuite}

@Config(sdk = Array(LOLLIPOP))
class TestSpec extends FlatSpec with Matchers with RobolectricSuite {
  "Resources" should "be accessible via R" in {
    RuntimeEnvironment.application.getString(R.string.app_name) shouldBe "bitrader"
  }

}