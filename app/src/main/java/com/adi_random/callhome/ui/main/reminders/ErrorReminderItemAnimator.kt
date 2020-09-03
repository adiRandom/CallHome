package com.adi_random.callhome.ui.main.reminders

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by Adrian Pascu on 02-Sep-20
 */
class ErrorReminderItemAnimator : DefaultItemAnimator() {
    override fun animateAppearance(
        viewHolder: RecyclerView.ViewHolder,
        preLayoutInfo: ItemHolderInfo?,
        postLayoutInfo: ItemHolderInfo
    ): Boolean {
        val reminderViewHolder = viewHolder as ReminderViewHolder
        val reminder = reminderViewHolder.binding.model?.reminder
        if (reminder?.hasError == false)
            return super.animateAppearance(viewHolder, preLayoutInfo, postLayoutInfo)
        else {
            val lightUpAnim = ObjectAnimator.ofArgb(
                reminderViewHolder.binding.root,
                "background",
                -0x1,
                0x428E0101
            )
            val dimAnim = ObjectAnimator.ofArgb(
                reminderViewHolder.binding.root,
                "background",
                0x428E0101,
                -0x1
            )
            AnimatorSet().apply {
                play(dimAnim).after(lightUpAnim)
//                TODO: Add the initial background
                start()
            }
            return false
        }

    }
}