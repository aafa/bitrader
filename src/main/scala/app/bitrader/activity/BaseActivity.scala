package app.bitrader.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import app.bitrader.helpers.activity.ActivityOperations
import macroid.Contexts

/**
  * Created by Alex Afanasev
  */
abstract class BaseActivity extends AppCompatActivity with Contexts[AppCompatActivity]
  with ActivityOperations with Circuitable{

  override def onCreate(b: Bundle): Unit = super.onCreate(b)

}
