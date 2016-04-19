package app.bitrader

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.{FontAwesomeModule, MaterialModule}

/**
  * Created by Alexey Afanasev on 07.04.16.
  */
class ClientApplication extends Application{
  override def onCreate(): Unit = {
    super.onCreate()
    Iconify.`with`(new FontAwesomeModule).`with`(new MaterialModule)

  }
}
