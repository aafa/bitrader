package app.bitrader

import java.text.{DateFormat, SimpleDateFormat}

import android.app.Application
import android.content.Context
import api.UiService
import app.bitrader.api.poloniex.PoloniexAPIServiceDescriptor
import com.github.aafa.{DefaultRetrofitBuilder, ScalaRetrofitBuilder}
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.{FontAwesomeModule, MaterialModule}
import retrofit.RestAdapter

/**
  * Created by Alexey Afanasev on 07.04.16.
  */
class ClientApplication extends Application {

  override def onCreate(): Unit = {
    super.onCreate()
    Iconify.`with`(new FontAwesomeModule).`with`(new MaterialModule)

    APIContext.poloniexApi = buildApi(getApplicationContext)
  }

  def buildApi(implicit c: Context): PoloniexAPIServiceDescriptor = {
    new ScalaRetrofitBuilder(_.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")))
      .setEndpoint(TR.string.poloniex_url.value)
      .setLogLevel(RestAdapter.LogLevel.FULL)
      .build()
      .create(classOf[PoloniexAPIServiceDescriptor])
  }
}

// todo factory
object APIContext {
  var poloniexApi: PoloniexAPIServiceDescriptor = _
  def poloniexService: UiService[PoloniexAPIServiceDescriptor] = new UiService(poloniexApi)
}