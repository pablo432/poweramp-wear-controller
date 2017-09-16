package com.pdyjak.powerampwear.music_browser

import android.support.annotation.CallSuper
import android.view.View
import android.widget.TextView

import com.pdyjak.powerampwear.R

open class SimpleTwoLineItemViewHolderBase(itemView: View) : ItemViewHolder(itemView) {
    protected val mFirstLine: TextView = itemView.findViewById(R.id.first_line) as TextView
    protected val mSecondLine: TextView = itemView.findViewById(R.id.second_line) as TextView

    @CallSuper
    override fun bindWith(obj: Any?) {
        (obj as Clickable).let { item ->
            itemView.setOnClickListener { item.onClicked() }
        }
    }
}
