package app.bitrader.helpers

import android.graphics.drawable.Drawable
import android.graphics.{Color, Paint}
import android.support.design.widget.CoordinatorLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.CardView
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams._
import android.view.{View, ViewGroup}
import android.widget.AdapterView.OnItemClickListener
import android.widget._
import macroid.FullDsl._
import macroid._


/**
  * Created by Alexey Afanasev on 17.04.16.
  */

trait TweaksAndGoodies extends AppStyles with AdditionalTweaks

trait AppStyles extends Styles{

  def cardTweak(implicit cw: ContextWrapper): Tweak[CardView] = Tweak[CardView](c => {
    val p = 10.dp
    c.setRadius(5.dp)
    c.setCardElevation(3.dp)
    c.setContentPadding(p, p, p, p)
  }) + margin(all = 10.dp)


  def scrollable(cc: Ui[View]*)(implicit c: ContextWrapper): Ui[CoordinatorLayout] = {
    l[CoordinatorLayout](
      l[NestedScrollView](
        l[LinearLayout](
          cc: _*
        ) <~ vertical <~ vMatchParent
      ) <~ vMatchParent
    ) <~ vMatchParent
  }
}


trait Styles {
  val vMatchParent: Tweak[View] = lp[ViewGroup](MATCH_PARENT, MATCH_PARENT)

  val vWrapContent: Tweak[View] = lp[ViewGroup](WRAP_CONTENT, WRAP_CONTENT)

  val vMatchWidth: Tweak[View] = lp[ViewGroup](MATCH_PARENT, WRAP_CONTENT)

  val vMatchHeight: Tweak[View] = lp[ViewGroup](WRAP_CONTENT, MATCH_PARENT)

  def vFill(w: Long): Tweak[View] = lp[LinearLayout](MATCH_PARENT, MATCH_PARENT, w)

  val vFill: Tweak[View] = vFill(1)

  def vWrap(w: Long): Tweak[View] = lp[LinearLayout](WRAP_CONTENT, WRAP_CONTENT, w)

  val vWrap: Tweak[View] = vWrap(1)

  def vContentSizeMatchWidth(h: Int): Tweak[View] = lp[ViewGroup](MATCH_PARENT, h)

  def vContentSizeMatchHeight(w: Int): Tweak[View] = lp[ViewGroup](w, MATCH_PARENT)

  def fGravity(g: Int): Tweak[FrameLayout] = {
    val param = new FrameLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT )
    param.gravity = g
    new Tweak[FrameLayout](_.setLayoutParams(param))
  }

  def llGravity(g: Int): Tweak[LinearLayout] = Tweak[LinearLayout](_.setGravity(g))

  def rGravity(g: Int): Tweak[RelativeLayout] = Tweak[RelativeLayout](_.setGravity(g))

  val llTableStyle: Tweak[LinearLayout] = vertical + vMatchParent

  val llRowStyle: Tweak[LinearLayout] = vertical + vMatchWidth

  def onClick[A <: View](handler: => Any): Tweak[A] = On.click {
    handler
    Ui.nop
  }

  def onItemClick(listener: Int => Unit) = Tweak[ListView](_.setOnItemClickListener(new OnItemClickListener {
    override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long): Unit =
      listener(position)
  }))

  def img[A <: ImageView](resourceId: Int) = Tweak[A](_.setImageResource(resourceId))

  def drawable[A <: ImageView](d: Drawable) = Tweak[A](_.setImageDrawable(d))

  def bgColor[A <: View](color: Int) = Tweak[A](_.setBackgroundColor(color))

  def bgColorR[A <: View](color: Int)(implicit ctx: ContextWrapper) = Tweak[A](_.setBackgroundColor(ctx.bestAvailable.getResources.getColor(color)))

  def bg[A <: View](res: Drawable) = Tweak[A](_.setBackgroundDrawable(res))

  def bgR[A <: View](res: Int)(implicit ctx: ContextWrapper) = Tweak[A](_.setBackgroundDrawable(ctx.bestAvailable.getResources.getDrawable(res)))

  def textColor[A <: TextView](color: Int) = Tweak[A](_.setTextColor(color))

  def textMaybe[A <: TextView](t: Option[String]): Tweak[TextView] = show(t.nonEmpty) + text(t.getOrElse(""))

  def textColorR[A <: TextView](color: Int)(implicit ctx: ContextWrapper) = Tweak[A](_.setTextColor(ctx.bestAvailable.getResources.getColor(color)))

  def textSize[A <: TextView](s: Float) = Tweak[A](_.setTextSize(s))

  def striked = Tweak[TextView](tv => tv.setPaintFlags(tv.getPaintFlags | Paint.STRIKE_THRU_TEXT_FLAG))

  def debug = bgColor[View](Color.BLACK)

  def margin(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0, all: Int = -1) = if (all >= 0) {
    llLayoutMargin(all, all, all, all)
  } else {
    llLayoutMargin(left, top, right, bottom)
  }

  def llLayoutMargin(
                      marginLeft: Int = 0,
                      marginTop: Int = 0,
                      marginRight: Int = 0,
                      marginBottom: Int = 0): Tweak[View] = Tweak[View] {
    view ⇒
      val params = new LinearLayout.LayoutParams(view.getLayoutParams)
      params.setMargins(marginLeft, marginTop, marginRight, marginBottom)
      view.setLayoutParams(params)
  }

  def emptyView(implicit c: ContextWrapper) = w[View] <~ lp[ViewGroup](0,0)



}