import app.bitrader.api.ApiProvider
import app.bitrader.api.apitest.ApiTest
import app.bitrader.api.bitfinex.Bitfinex
import com.mikepenz.materialdrawer.model.ProfileDrawerItem

val providers = Set(ApiTest, Bitfinex)
providers map (_.toString)
providers.size

def prov(k: Any): ProfileDrawerItem = {
  new ProfileDrawerItem().withName(k.toString)
}

val items = providers map (k => prov(k))
println(items map (_.getName))


