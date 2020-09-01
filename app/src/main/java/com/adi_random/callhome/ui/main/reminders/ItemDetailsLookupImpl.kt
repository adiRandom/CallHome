package com.adi_random.callhome.ui.main.reminders

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by Adrian Pascu on 31-Aug-20
 */
class ItemDetailsLookupImpl(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        return if (view != null) {
            (recyclerView.getChildViewHolder(view) as ReminderViewHolder).getItemDetails()
        } else null
    }
}