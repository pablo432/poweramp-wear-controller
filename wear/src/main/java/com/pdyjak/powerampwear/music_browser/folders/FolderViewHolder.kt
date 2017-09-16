package com.pdyjak.powerampwear.music_browser.folders

import android.view.View

import com.pdyjak.powerampwear.music_browser.SimpleTwoLineItemViewHolderBase

internal class FolderViewHolder(itemView: View) : SimpleTwoLineItemViewHolderBase(itemView) {

    override fun bindWith(obj: Any?) {
        super.bindWith(obj)
        val item = obj as FolderItem
        mFirstLine.text = item.name
        mSecondLine.text = item.parentName
    }
}
