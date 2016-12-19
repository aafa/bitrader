package app.bitrader.storage

import android.content.Context

/**
  * Created by Alex Afanasev
  */
trait BinaryStored {
  def ctx : Context

  ctx.getCacheDir
}
