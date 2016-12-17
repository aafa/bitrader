package app.bitrader.activity.layouts

import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view._
import app.bitrader._
import app.bitrader.activity.fragments.TestFragment
import app.bitrader.activity.menu.{ReadQrActivity, WampActivity}
import app.bitrader.activity.{CurrencyListActivity, MainActivity, MainActivityLayoutInflated}
import app.bitrader.api.ApiProvider
import app.bitrader.helpers._
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.materialdrawer.AccountHeader.OnAccountHeaderListener
import com.mikepenz.materialdrawer.Drawer.OnDrawerItemClickListener
import com.mikepenz.materialdrawer.model.interfaces.{IDrawerItem, IProfile}
import com.mikepenz.materialdrawer.model.{PrimaryDrawerItem, ProfileDrawerItem, ProfileSettingDrawerItem}
import com.mikepenz.materialdrawer.{AccountHeader, AccountHeaderBuilder, Drawer, DrawerBuilder}
import macroid._
import macroid.FullDsl._

import scala.language.postfixOps
import scala.util.Random

/**
  * Created by Alex Afanasev
  */
class DrawerLayout(appCircuit: ICircuit, l: MainActivityLayoutInflated)
                  (implicit cw:ContextWrapper, managerContext: FragmentManagerContext[Fragment, FragmentManager])
  extends BasicLayout {

  def profileWrapper(k: ApiProvider): ProfileDrawerItem = {
    new ProfileDrawerItem().withName(k.toString).withIdentifier(Random.nextLong())  // inject random id to have them distinct
  }

  lazy val providers: Seq[ApiProvider] = appCircuit.zoom(_.serviceContext).value.keys.toSeq
  lazy val profileItems: Map[ApiProvider, ProfileDrawerItem] = providers zip (providers map profileWrapper) toMap
  lazy val apiKey: Map[ProfileDrawerItem, ApiProvider] = profileItems.map(_.swap)

  lazy val menuItems: Seq[IProfile[_]] = profileItems.values.toSeq :+
    new ProfileSettingDrawerItem().withName("Add profile").withIcon(GoogleMaterial.Icon.gmd_add)


  def drawerSetup(mainActivity: MainActivity): Unit = {
    val accountHeader: AccountHeader = new AccountHeaderBuilder()
      .withActivity(mainActivity)
      .addProfiles(menuItems: _*)
      .withOnAccountHeaderListener(new OnAccountHeaderListener {
        override def onProfileChanged(view: View, item: IProfile[_], b: Boolean): Boolean = {
          item match {
            case p: ProfileDrawerItem => appCircuit(SelectApi(apiKey(p)))
            case s: ProfileSettingDrawerItem => // todo settings
          }

          true
        }
      })
      .withHeaderBackground(R.drawable.material_flat)
      .build()

    accountHeader.setActiveProfile(profileItems(appCircuit.zoom(_.selectedApi).value))

    type IDrawer = IDrawerItem[_, _ <: ViewHolder]
    def itemWrapper(s: String) = new PrimaryDrawerItem().withName(s)
      .withSelectable(false).withIdentifier(Random.nextLong())

    val actions: Map[IDrawer, () => Unit] = Map(
      itemWrapper("Wamp") -> { () => startActivity[WampActivity] },
      itemWrapper("Test") -> { () => l.insertFragment(f[TestFragment]) },
      itemWrapper("CurrencyListActivity") -> { () => startActivity[CurrencyListActivity] },
      itemWrapper("Read qr") -> { () => startActivity[ReadQrActivity] }
    )

    val drawer: Drawer = new DrawerBuilder().withActivity(mainActivity)
      .withToolbar(mainActivity.layout.toolbarView)
      .withAccountHeader(accountHeader)
      .addDrawerItems(actions.keys.toSeq: _*)
      .withOnDrawerItemClickListener(new OnDrawerItemClickListener {
        override def onItemClick(view: View, i: Int, item: IDrawer): Boolean = {
          actions(item).apply()
          true
        }
      })
      .withCloseOnClick(true)
      .build()

    drawer.setSelection(-1)

  }
}
