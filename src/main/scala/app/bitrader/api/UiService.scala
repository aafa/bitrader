package app.bitrader.api

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Alexey Afanasev on 21.04.16.
  */

class UiService[API](api: API) {
  def apply[R](f: API => R): Future[R] = Future {
    f(api)
  }
}