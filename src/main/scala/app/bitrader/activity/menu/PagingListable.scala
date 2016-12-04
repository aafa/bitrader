package app.bitrader.activity.menu

import android.view.View
import android.widget.AbsListView.OnScrollListener
import android.widget.{AbsListView, AdapterView}
import android.widget.AdapterView.OnItemClickListener
import app.bitrader.helpers.UiThreading
import macroid.{ContextWrapper, Tweak}
import macroid.viewable.{Listable, ListableListAdapter}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Alex Afanasev
  */
trait PagingListable extends UiThreading{

  def pagingAdapterTweak[A](loadMoreItems: Long => Future[(Seq[A], Long)], listable: Listable[A, _], dataLoaded: Long => Unit)
                           (implicit c: ContextWrapper, ec: ExecutionContext): Future[Tweak[AbsListView]] = {
    loadMoreItems(0) mapUi { case (firstPage: Seq[A], total: Long) =>
      listable.listAdapterTweak(firstPage) + Tweak[AbsListView](l => {
        var isLoading = false
        var totalOutThere = total

        dataLoaded(total)

        def load(a: ListableListAdapter[A, _], lastVisibleItem: Int): Unit = {
          isLoading = true
          loadMoreItems(lastVisibleItem) map {
            case (nextPage: Seq[A], total: Long) =>
              isLoading = false
              totalOutThere = total
              a.addAll(nextPage: _*)
          }
        }

        l.setOnScrollListener(new OnScrollListener {
          override def onScrollStateChanged(absListView: AbsListView, i: Int): Unit = {}

          override def onScroll(absListView: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalLoadedItemCount: Int): Unit = {
            val lastVisibleItem = firstVisibleItem + visibleItemCount

            absListView.getAdapter match {
              case a: ListableListAdapter[A, _] if lastVisibleItem == totalLoadedItemCount
                && !isLoading && totalOutThere > totalLoadedItemCount =>
                load(a, lastVisibleItem)
              case _ =>
            }
          }
        })
      }
      )
    }
  }


  def adapterOnClick[A](onclick: A => Unit)(implicit c: ContextWrapper) = {
    Tweak[AbsListView](l => {
      l.setOnItemClickListener(new OnItemClickListener {
        override def onItemClick(adapterView: AdapterView[_], view: View, i: Int, l: Long): Unit = {
          adapterView.getAdapter match {
            case a: ListableListAdapter[A, _] => onclick(a.getItem(i))
            case _ =>
          }
        }
      })
    })
  }
}