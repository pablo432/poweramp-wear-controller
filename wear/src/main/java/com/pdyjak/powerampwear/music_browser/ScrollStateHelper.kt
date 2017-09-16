package com.pdyjak.powerampwear.music_browser

import android.os.Parcelable
import android.support.v7.widget.RecyclerView

class ScrollStateHelper {
    private var mState: Parcelable? = null

    fun save(recyclerView: RecyclerView?) {
        mState = recyclerView?.layoutManager?.onSaveInstanceState()
    }

    fun restoreTo(recyclerView: RecyclerView?) {
        if (recyclerView == null || mState == null) return
        val state = mState
        mState = null
        recyclerView.post { recyclerView.layoutManager.onRestoreInstanceState(state) }
    }
}
