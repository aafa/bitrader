package app.bitrader

import java.util.Properties

import android.app.Application
import android.content.Context
import app.bitrader.api._
import app.bitrader.api.apitest.ApiTest
import app.bitrader.api.bitfinex.Bitfinex
import app.bitrader.api.common.{AuthData, UserProfile}
import app.bitrader.api.poloniex.Poloniex
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.{FontAwesomeModule, MaterialModule}
import com.orhanobut.logger.Logger

/**
  * Created by Alexey Afanasev on 07.04.16.
  */
class ClientApplication extends Application {

  val diModule: DiModule = DiModuleProd

  override def onCreate(): Unit = {
    super.onCreate()
    Iconify.`with`(new FontAwesomeModule).`with`(new MaterialModule)

    Logger.init("Bitrader")

    AppContext.appContext = getApplicationContext
    AppContext.diModule = diModule
    AppContext.onCreate
  }

}

object AppContext {
  implicit var appContext: Context = _
  var diModule: DiModule = _

  lazy val jacksonMapper = {
    val jm = new ObjectMapper() with ScalaObjectMapper
    jm.registerModule(DefaultScalaModule)
    jm.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    jm
  }

  // todo memoize smart
  lazy val apis: Map[ApiProvider, AbstractFacade] = Map(
    Poloniex -> NetworkFacade.factory(Poloniex),
    ApiTest -> NetworkFacade.factory(ApiTest)
    //    Bitfinex -> NetworkFacade.factory(Bitfinex)
  )

  lazy val accounts: Seq[Account] = Seq(
    Account(Poloniex, ApiContext(theme = R.style.MainTheme)),
    Account(Bitfinex, ApiContext(theme = R.style.GreenTheme))
  )

  def getService(api: ApiProvider): UiService[AbstractFacade] = new UiService(get(api))


  def get(api: ApiProvider): AbstractFacade = apis(api)

  def onCreate = {
    val debugAcc = Account(Poloniex, ApiContext(theme = R.style.MainTheme,
      auth = UserProfile(authData = Some(AuthData(
        apiKey = LocalProperties.apiKey,
        apiSecret = LocalProperties.apiSecret
      )))))

    diModule.appCircuit(AddAccount(debugAcc))
  }

  object LocalProperties {
    lazy val prop = {
      val p = new Properties()
      val resourceAsStream = appContext.getAssets.open("local.properties")
      p.load(resourceAsStream)
      p
    }

    val apiKey = prop.getProperty("apiKey")
    val apiSecret = prop.getProperty("apiSecret")

  }

}

trait DiModule {
  val appCircuit: ICircuit
}

object DiModuleProd extends DiModule {
  val appCircuit = new AppCircuit
}