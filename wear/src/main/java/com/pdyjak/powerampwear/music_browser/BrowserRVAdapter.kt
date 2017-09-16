package com.pdyjak.powerampwear.music_browser

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import java.util.ArrayList
import java.util.Collections

class BrowserRVAdapter<T>(private val mFactory: ViewHolderFactory)
    : RecyclerView.Adapter<ItemViewHolder>() {

    private val mItems = ArrayList<T>()

    var items: List<T>
        get() = Collections.unmodifiableList(mItems)
        set(items) {
            if (mItems == items) return
            mItems.clear()
            mItems.addAll(items)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return mFactory.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val obj = mItems[position]
        holder.bindWith(obj)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }
}
