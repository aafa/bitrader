package app.bitrader.activity


import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.{SearchView, Toolbar}
import android.view.{Menu, MenuItem, View}
import app.bitrader.{R, TR}
import macroid.FullDsl._
import macroid.{Contexts, Tweak}


class TestActivity extends AppCompatActivity with Contexts[AppCompatActivity] with MainStyles{

  override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    setContentView(R.layout.app_bar)

    val toolbar: Toolbar = findViewById(TR.toolbar.id).asInstanceOf[Toolbar]
    toolbar.setTitle("Main activity")
    setSupportActionBar(toolbar)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    super.onOptionsItemSelected(item)
    println("item selected! " + item.getTitle)
    true
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    super.onCreateOptionsMenu(menu)

    getMenuInflater.inflate(R.menu.menu, menu)

//    val search: MenuItem = menu.add("search")
//    search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//    search.setActionView(
//      (w[SearchView] <~ Tweak[SearchView](sv => {
//        sv.setQueryHint("coin pairs")
//      }) <~ vMatchWidth).get
//    )
    true
  }

}
