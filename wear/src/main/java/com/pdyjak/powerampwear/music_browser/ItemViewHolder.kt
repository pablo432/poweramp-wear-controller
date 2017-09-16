package com.pdyjak.powerampwear.music_browser

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindWith(obj: Any?)
}
