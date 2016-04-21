package app.bitrader

import android.app.Application
import api.{APIServiceDescriptor, UiService}
import com.github.aafa.DefaultRetrofitBuilder
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

    implicit val c = getApplicationContext
    APIContext.api = new DefaultRetrofitBuilder()
      .setEndpoint(TR.string.app_url.value)
      .setLogLevel(RestAdapter.LogLevel.FULL)
      .build()
      .create(classOf[APIServiceDescriptor])
  }
}

object APIContext {
  var api: APIServiceDescriptor = _
  def service: UiService = new UiService(api)
}