package app.bitrader.storage

import android.content.Context
import app.bitrader._


/**
  * Created by Alex Afanasev
  */
trait PreferenceStored {
  def ctx : Context

  val myClass: String = this.getClass.toString

  def save(): Unit = {
    val json: String = AppContext.jacksonMapper.writeValueAsString(this)
    ctx.saveValue(_.putString(myClass, json))
  }

  def delete(): Unit = {
    ctx.saveValue(_.remove(myClass))
  }

  def restore: Option[this.type] = {
    val string: String = ctx.preferences.getString(myClass, null)
    if (string != null) {
      val me = AppContext.jacksonMapper.readValue[this.type](string)
      Some(me)
    } else {
      None
    }
  }
}

