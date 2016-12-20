package app.bitrader.activity.layouts

import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view._
import app.bitrader._
import app.bitrader.activity.fragments.{PairsListFragment, PortfolioFragment, TestFragment}
import app.bitrader.activity.menu.{ReadQrActivity, WampActivity}
import app.bitrader.activity.{MainActivity, MainActivityLayoutInflated}
import app.bitrader.api.ApiProvider
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.materialdrawer.AccountHeader.OnAccountHeaderListener
import com.mikepenz.materialdrawer.Drawer.OnDrawerItemClickListener
import com.mikepenz.materialdrawer.model.interfaces.{IDrawerItem, IProfile}
import com.mikepenz.materialdrawer.model.{PrimaryDrawerItem, ProfileDrawerItem, ProfileSettingDrawerItem}
import com.mikepenz.materialdrawer.{AccountHeader, AccountHeaderBuilder, Drawer, DrawerBuilder}
import macroid.FullDsl._
import macroid._

import scala.language.postfixOps
import scala.util.Random

/**
  * Created by Alex Afanasev
  */
class DrawerLayout(appCircuit: ICircuit, l: MainActivityLayoutInflated)
                  (implicit cw: ContextWrapper, managerContext: FragmentManagerContext[Fragment, FragmentManager])
  extends BasicLayout {
  type IDrawer = IDrawerItem[_, _ <: ViewHolder]

  def profileWrapper(acc: Account): ProfileDrawerItem = {
    new ProfileDrawerItem().withName(acc.name).withIdentifier(Random.nextLong()) // inject random id to have them distinct
  }

  lazy val providers: Seq[Account] = appCircuit.zoom(_.serviceContext).value
  lazy val profileItems: Map[Account, ProfileDrawerItem] = providers zip (providers map profileWrapper) toMap
  lazy val apiKey: Map[ProfileDrawerItem, Account] = profileItems.map(_.swap)

  lazy val profiles: Seq[IProfile[_]] = profileItems.values.toSeq :+
    new ProfileSettingDrawerItem().withName("Add profile").withIcon(GoogleMaterial.Icon.gmd_add)


  def drawerSetup(mainActivity: MainActivity): Drawer = {
    val accountHeader: AccountHeader = new AccountHeaderBuilder()
      .withActivity(mainActivity)
      .addProfiles(profiles: _*)
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

    accountHeader.setActiveProfile(profileItems(appCircuit.zoom(_.selectedAccount).value))


    def item(s: String): PrimaryDrawerItem = new PrimaryDrawerItem().withName(s)
      .withSelectable(true).withIdentifier(Random.nextLong())

    val menuItems: Array[DrawerMenuItem] = Array(
      DrawerMenuItem(item("Wamp"), action = () => startActivity[WampActivity]),
      DrawerMenuItem(item("Test"), f[TestFragment]),
      DrawerMenuItem(item("CurrencyListActivity"), f[PairsListFragment]),
      DrawerMenuItem(item("Balances"), f[PortfolioFragment]),
      DrawerMenuItem(item("Read qr"), action = () => startActivity[ReadQrActivity])
    )

    val drawer: Drawer = new DrawerBuilder().withActivity(mainActivity)
      .withToolbar(mainActivity.layout.toolbarView)
      .withAccountHeader(accountHeader)
      .addDrawerItems(menuItems.map(_.item): _*)
      .withOnDrawerItemClickListener(new OnDrawerItemClickListener {
        override def onItemClick(view: View, i: Int, item: IDrawer): Boolean = {
          menuItems(i - 1).call()
          true
        }
      })
      .withCloseOnClick(true)
      .withDelayDrawerClickEvent(5)
      .build()

    drawer.setSelection(1)
    drawer
  }

  case class DrawerMenuItem(item: IDrawer,
                            f: FragmentBuilder[_ <: Fragment] = null,
                            action: () => Unit = () => ()
                           ) {
    def call() = {
      appCircuit(SetMainFragment(f))
      action()
    }

    def title: String = ???
  }

}


