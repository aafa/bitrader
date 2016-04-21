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


class MainActivity2 extends SingleViewDrawerActivity with ActivityOperations {

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    setContentView(layout.ui.get)

    layout.toolBar map setSupportActionBar
  }

  override val menuItems = Seq(
    DrawerMenuItem("User profile", action = () => startActivity[MainActivity]),
    DrawerMenuItem("Tabs", action = () => startActivity[TabActivity])
  )

  override def frontFragment: FragmentBuilder[_ <: Fragment] = f[PairsList]

}
