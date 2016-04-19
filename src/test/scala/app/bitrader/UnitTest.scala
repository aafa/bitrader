package app.bitrader

import app.bitrader.R
import android.os.Build.VERSION_CODES.LOLLIPOP
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.scalatest.{FlatSpec, Matchers, RobolectricSuite}

@Config(sdk = Array(LOLLIPOP))
class UnitTest extends FlatSpec with Matchers with RobolectricSuite {
  "Resources" should "be accessible via R" in {
    RuntimeEnvironment.application.getString(R.string.app_name) shouldBe "material"
  }

}