package app.bitrader.helpers.activity

import android.app.Activity
import android.content.Intent
import macroid.ContextWrapper

import scala.collection.mutable
import scala.reflect._

/**
  * Created by Alexey Afanasev on 16.02.16.
  */
trait ActivityOperations {
  @inline def startActivity[T: ClassTag](implicit context: ContextWrapper): Unit = context.bestAvailable.startActivity(IntentObject[T])

  @inline def startActivityWithParams[A <: Activity : ClassTag](o: Passable*)(implicit context: ContextWrapper): Unit = {
    if (o != null) {
      PassParams.array ++= o
    } else {
      PassParams.array.clear()
    }
    this.startActivity[A]
  }

  @inline def getParam[T <: Passable : ClassTag]: Option[T] = {
    val maybePassable: Option[T] = PassParams.array.find(p => classTag[T].runtimeClass.isInstance(p)).asInstanceOf[Option[T]]
    PassParams.array.clear() // todo remove only pick
    maybePassable
  }

  implicit class ActivityHelper(a: Activity) {
    def changeTheme(theme: Int) = {
      a.setTheme(theme)
      a.finish()
      a.startActivity(new Intent(a, a.getClass))
    }
  }
}

object IntentObject {
  @inline def apply[T](implicit context: ContextWrapper, mt: ClassTag[T]) = new Intent(context.bestAvailable, mt.runtimeClass)
}

object PassParams {
  var array = mutable.ArrayBuffer[Passable]()
}

trait Passable

