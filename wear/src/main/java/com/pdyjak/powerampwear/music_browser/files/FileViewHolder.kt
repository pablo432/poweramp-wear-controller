package com.pdyjak.powerampwear.music_browser.files

import android.view.View

import com.pdyjak.powerampwear.music_browser.SimpleTwoLineItemViewHolderBase

import java.util.Locale

internal class FileViewHolder(itemView: View) : SimpleTwoLineItemViewHolderBase(itemView) {

    override fun bindWith(obj: Any?) {
        super.bindWith(obj)
        val item = obj as FileItem
        val sb = StringBuilder()
        val hours = item.duration / 3600
        val minutes = item.duration % 3600 / 60
        val seconds = item.duration % 60
        val formattingStr = "%02d"
        if (hours > 0) sb.append(String.format(Locale.US, formattingStr, hours)).append(":")
        sb.append(String.format(Locale.US, formattingStr, minutes)).append(":")
        sb.append(String.format(Locale.US, formattingStr, seconds))

        mFirstLine.text = String.format(Locale.US, "%s : %s", item.title, sb.toString())
        mSecondLine.text = String.format(Locale.US, "%s - %s", item.artist, item.album)
    }
}
