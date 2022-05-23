package com.aligmohammad.nadsoftmvvm.framework.presentation.bclist

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.aligmohammad.nadsoftmvvm.framework.presentation.bclist.state.BusinessCardListInteractionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class BusinessCardItemTouchHelperCallback
constructor(
    private val itemTouchHelperAdapter: ItemTouchHelperAdapter,
    private val businesscardListInteractionManager: BusinessCardListInteractionManager
): ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(
            ItemTouchHelper.ACTION_STATE_IDLE,
            ItemTouchHelper.START or ItemTouchHelper.END
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        itemTouchHelperAdapter.onItemSwiped(viewHolder.adapterPosition)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return !businesscardListInteractionManager.isMultiSelectionStateActive()
    }

}


interface ItemTouchHelperAdapter{

    fun onItemSwiped(position: Int)
}















