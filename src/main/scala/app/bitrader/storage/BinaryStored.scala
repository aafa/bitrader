package app.bitrader.storage

import android.content.Context
import boopickle.Default._

/**
  * Created by Alex Afanasev
  */
trait BinaryStored {
  def ctx : Context

  ctx.getCacheDir
}
