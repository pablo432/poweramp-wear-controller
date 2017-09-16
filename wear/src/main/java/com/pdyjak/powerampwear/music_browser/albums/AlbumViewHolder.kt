package com.pdyjak.powerampwear.music_browser.albums

import android.view.View
import android.widget.TextView

import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.music_browser.ItemViewHolder

internal class AlbumViewHolder(itemView: View) : ItemViewHolder(itemView) {
    private val mFirstLine: TextView
    private val mSecondLine: TextView
    private var mCurrent: AlbumItem? = null

    init {
        mFirstLine = itemView.findViewById(R.id.first_line) as TextView
        mSecondLine = itemView.findViewById(R.id.second_line) as TextView
        itemView.setOnClickListener { mCurrent?.onClicked() }
    }

    override fun bindWith(obj: Any?) {
        mCurrent = (obj as AlbumItem)
        mFirstLine.text = mCurrent!!.name
        mSecondLine.text = mCurrent!!.artist
    }
}
