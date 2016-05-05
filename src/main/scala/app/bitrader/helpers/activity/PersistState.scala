package app.bitrader.helpers.activity

import android.app.Activity
import android.os.Parcelable.Creator
import android.os.{Bundle, Parcel, Parcelable}

/**
  * Created by Alex Afanasev
  */
class PersistState {

}

trait PreserveState extends ParcelableActivity{
  this: Activity =>

  private val parcelName: String = "out"

//  override def onSaveInstanceState(outState: Bundle): Unit = {
//    this.onSaveInstanceState(outState)
//    outState.putParcelable(parcelName, this)
//  }
//
//  override def onRestoreInstanceState(savedInstanceState: Bundle): Unit = {
//    this.onRestoreInstanceState(savedInstanceState)
////    this = savedInstanceState.getParcelable(parcelName)
//  }
}

trait ParcelableActivity extends Parcelable{
  private var _data: Int = 0

  override def writeToParcel(parcel: Parcel, i: Int): Unit = {
    parcel.writeInt(_data)
  }

  override def describeContents(): Int = 0

  val CREATOR : Parcelable.Creator[MyParcelable] = new Parcelable.Creator[MyParcelable]() {
    override def newArray(i: Int): Array[MyParcelable] = new Array[MyParcelable](i)
    override def createFromParcel(parcel: Parcel): MyParcelable = new MyParcelable(parcel)
  }

  class MyParcelable(in : Parcel){
    _data = in.readInt()
  }

}