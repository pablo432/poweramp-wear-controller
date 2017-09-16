package com.pdyjak.powerampwear.music_browser

import android.view.ViewGroup

interface ViewHolderFactory {
    fun createViewHolder(parent: ViewGroup): ItemViewHolder
}
