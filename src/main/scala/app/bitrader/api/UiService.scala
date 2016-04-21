package app.bitrader.api
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Alexey Afanasev on 21.04.16.
  */

class UiService(api: APIServiceDescriptor) {
  def apply[T](f: APIServiceDescriptor => T): Future[T] = Future {
    f(api)
  }
}