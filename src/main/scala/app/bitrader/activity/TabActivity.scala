//package app.bitrader.activity
//
//import android.app.Activity
//import android.os.Bundle
//import android.support.design.widget.TabLayout
//import android.support.v4.view.ViewPager
//import android.support.v7.app.AppCompatActivity
//import android.view.View
//import android.view.View.OnClickListener
//import android.widget.{LinearLayout, TextView}
//import app.bitrader.R
//import app.bitrader.helpers.{AwesomeIcon, Styles}
//import com.joanzapata.iconify.fonts.MaterialIcons
//import macroid.{Contexts, Tweak, Ui}
//import macroid.FullDsl._
//import macroid._
//import macroid.LayoutDsl.{l => _, slot => _, w => _}
//import macroid.Tweaks.{show => _, text => _, vertical => _, wire => _}
//import macroid.contrib.TextTweaks
//import macroid.viewable._
//
///**
//  * Created by Alexey Afanasev on 18.03.16.
//  */
//class TabActivity extends AppCompatActivity with Contexts[Activity] with TabView {
//
//  override def onCreate(savedInstanceState: Bundle): Unit = {
//    super.onCreate(savedInstanceState)
//    setContentView(ui)
//
//    toolBar map setSupportActionBar
//    toolBar.get.setNavigationIcon(AwesomeIcon(MaterialIcons.md_close))
//    toolBar.get.setNavigationOnClickListener(new OnClickListener {
//      override def onClick(view: View): Unit = TabActivity.this.onBackPressed()
//    })
//
//    Ui.run(tabLayoutSlot <~ show <~ Tweak[TabLayout](_.setupWithViewPager(vpSlot.get)))
//  }
//}
//
//trait TabView extends Styles with ToolbarStyle {
//  this: TabActivity =>
//
//  var vpSlot = slot[ViewPager]
//
//  val pages = Vector(
//    "page1" -> Page("1", "first page"),
//    "page2" -> Page("2", "second page")
//  )
//
//  def ui: View = {
//    l[LinearLayout](
//      toolbarLayout,
//      w[ViewPager] <~ wire(vpSlot) <~ pageViewable.pagerAdapterTweak(pages) <~ vMatchParent
//    ) <~ vMatchParent <~ vertical
//  }.get
//
//  case class Page(name: String, content: String)
//
//  def pageViewable: Viewable[Page, TextView] =
//    Viewable[Page] { p ⇒
//      w[TextView] <~ TextTweaks.large <~ text(p.content)
//    }
//
//}