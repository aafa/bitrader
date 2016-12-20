package app

import android.content.SharedPreferences.Editor
import android.content.{Context, SharedPreferences}
import app.bitrader.ApiContext
import app.bitrader.api.ApiProvider
import app.bitrader.api.poloniex.Currency
import app.bitrader.helpers.{Id, TweaksAndGoodies}
import app.bitrader.helpers.activity.ActivityOperations
import com.github.nscala_time.time.Imports._
import macroid.contrib.Layouts.RootFrameLayout
import macroid.{ContextWrapper, FragmentBuilder, FragmentManagerContext, Tag, Ui}

import scala.collection.SortedMap
import scala.language.implicitConversions
import scala.util.Random

/**
  * Created by Alexey Afanasev on 17.04.16.
  */
package object bitrader extends ActivityOperations with TweaksAndGoodies with JodaTimeHelpers with CommonTypes {

  implicit class WrapTRContext[A](tr: TypedRes[A]) {
    def get(implicit ev: TypedResource.TypedResValueOp[A], c: ContextWrapper): ev.T =
      tr.value(ev, c.bestAvailable).asInstanceOf[ev.T]
  }

  implicit class FragmentBuilderWrapper[F](f: FragmentBuilder[F]){
    def ui[M](implicit managerCtx: FragmentManagerContext[F, M]): Ui[RootFrameLayout] = {
      f.framed(Random.nextInt(1000), Tag.fragment)
    }
  }

  implicit class ContextHelper(ctx: Context) {
    def preferences: SharedPreferences = {
      val sharedPreferences: SharedPreferences = ctx.getSharedPreferences("appPreferences", 0)
      sharedPreferences
    }

    def saveValue(op: Editor => Unit) = {
      val edit: Editor = preferences.edit()
      op(edit)
      edit.commit()
    }
  }

}

trait ObjectEnum[A] {

  trait Value {
    self: A =>
    _values :+= this
  }

  private var _values = List.empty[A]

  def values = _values
}


trait CommonTypes {
  type OrderKey = String
  type OrderValue = BigDecimal
  type OrderPair = (OrderKey, OrderValue)
  type OrdersMap = SortedMap[OrderKey, OrderValue]
  type CurrenciesList = Map[String, Currency]
  type BalancesList = Map[String, String]
  type Balance = (String, String)

  implicit class EmptyOrderInt(k: Int) {
    implicit def convert(k : Int): OrderKey = k.toString
    def emptyPair: OrderPair = (k, 0)
  }

  implicit class EmptyOrder(k: OrderKey) {
    def emptyPair: OrderPair = (k, 0)
  }

}

trait JodaTimeHelpers {

  implicit class EpochSeconds(dt: DateTime) {
    def unixtime: Long = dt.getMillis / 1000
  }

  implicit class EpochDateTime(unixtime: Long) {
    def utime: DateTime = new DateTime(unixtime * 1000)

    def utimeFormatted: String = utime.toString("dd.MM.yyyy HH:mm")
  }

}