package app.bitrader.api

import android.content.Context
import app.bitrader.api.poloniex.PoloniexAPIServiceDescriptor
import app.bitrader.{APIContext, TR}
import com.github.aafa.DefaultRetrofitBuilder
import org.robolectric.RuntimeEnvironment
import org.scalatest.{FlatSpec, Matchers, RobolectricSuite}
import retrofit.RestAdapter

/**
  * Created by Alex Afanasev
  */
abstract class ApiSpec extends FlatSpec with Matchers with RobolectricSuite{

  implicit val c: Context = RuntimeEnvironment.application
  lazy val poloniexApi: PoloniexAPIServiceDescriptor = new DefaultRetrofitBuilder()
    .setEndpoint(TR.string.poloniex_url.value)
    .setLogLevel(RestAdapter.LogLevel.FULL)
    .build()
    .create(classOf[PoloniexAPIServiceDescriptor])

  lazy val poloniexService: UiService[PoloniexAPIServiceDescriptor] = new UiService(poloniexApi)

}
