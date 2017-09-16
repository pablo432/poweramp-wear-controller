package com.pdyjak.powerampwear.music_browser.artists

import android.view.View

import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.music_browser.SimpleTwoLineItemViewHolderBase

class ArtistViewHolder(itemView: View) : SimpleTwoLineItemViewHolderBase(itemView) {

    override fun bindWith(obj: Any?) {
        super.bindWith(obj)
        (obj as ArtistItem).let {
            mFirstLine.text = it.name
            mSecondLine.text = itemView.context.getString(R.string.songs_count_format,
                    it.songsCount)
        }
    }
}
