package app.bitrader.activity


import android.os.Bundle
import android.support.v4.app.Fragment
import app.bitrader.R
import app.bitrader.activity.menu.PairsList
import app.bitrader.helpers.ActivityOperations
import io.github.aafa.drawer.{DrawerMenuItem, SingleViewDrawerActivity}
import io.github.aafa.toolbar.ToolbarBelowLayout
import macroid.FullDsl._
import macroid._


class MainActivity extends SingleViewDrawerActivity with ToolbarBelowLayout with ActivityOperations {

  override val backgroundRes: Int = R.color.background_app
  override val toolbarThemeRes: Int = R.style.ThemeOverlay_AppCompat_Dark


  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    setContentView(ui.get)

    toolBar map setSupportActionBar
  }

  override val menuItems = Seq(
    DrawerMenuItem("User profile", action = () => startActivity[UserDetailsActivity]),
    DrawerMenuItem("Tabs", action = () => startActivity[TabActivity])
  )

  override lazy val drawerWidth: Int = this.getResources.getDimensionPixelSize(R.dimen.drawer_width)

  override def frontFragment: FragmentBuilder[_ <: Fragment] = f[PairsList]

}
