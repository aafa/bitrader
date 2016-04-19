package app.bitrader.helpers

import macroid.ContextWrapper

/**
  * Created by Alexey Afanasev on 17.04.16.
  */
trait BasicLayout {
  protected[this] implicit def cw: ContextWrapper
}
