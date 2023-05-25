package com.scatl.uestcbbs.helper

import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by sca_tl at 2023/5/25 9:35
 */
class VerticalDragHelper(val dragEnable: Boolean = true): ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        dragCallBack?.onItemMoved(viewHolder, target)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder?.let {
                ViewCompat.animate(it.itemView).setDuration(200).scaleX(1.1F).scaleY(1.1F).start()
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        ViewCompat.animate(viewHolder.itemView).setDuration(200).scaleX(1F).scaleY(1F).start()
        super.clearView(recyclerView, viewHolder)
    }

    override fun isItemViewSwipeEnabled() = false

    override fun isLongPressDragEnabled() = dragEnable

    interface DragCallBack {
        fun onItemMoved(viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder)
    }

    var dragCallBack: DragCallBack? = null
}