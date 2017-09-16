@file:Suppress("DEPRECATION") // for ProgressSpinner

package com.pdyjak.powerampwear.music_browser

import android.app.Fragment
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.LinearSnapHelper
import android.support.wearable.view.CurvedChildLayoutManager
import android.support.wearable.view.ProgressSpinner
import android.support.wearable.view.WearableRecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.musicLibraryCache
import com.pdyjak.powerampwear.settings.SettingsManager
import com.pdyjak.powerampwear.settingsManager

abstract class BrowserFragmentBase : Fragment() {

    companion object {
        const val SCROLL_DESTINATION_KEY = "scroll_to"
    }

    private inner class SettingsListener : SettingsManager.Listener() {
        override fun onCircularScrollingChanged() {
            mContentView!!.isCircularScrollingGestureEnabled =
                    activity.settingsManager.useCircularScrollingGesture()
        }
    }

    private inner class CacheInvalidationListener : MusicLibraryCache.InvalidationListener {
        override fun onCacheInvalidated() {
            mSpinner!!.visibility = View.VISIBLE
            mContentView!!.visibility = View.GONE
            fetchItems()
        }
    }

    protected abstract fun createViewHolderFactory(): ViewHolderFactory

    /**
     * Note: implementation, in case of success, should call setItems on base class before
     * returning true.
     * @return true, if items has been restored from cache
     */
    protected abstract fun tryRestoreCachedItems(): Boolean

    protected abstract fun fetchItems()

    // It's not abstract because I'm lazy to update all of them
    protected open fun onGoingToRefresh() {}

    protected open fun shouldScrollTo(item: Clickable, scrollDest: String): Boolean {
        return false
    }

    private val mScrollStateHelper = ScrollStateHelper()
    private val mSettingsListener = SettingsListener()
    private val mCacheInvalidationListener = CacheInvalidationListener()

    private var mAdapter: BrowserRVAdapter<Clickable>? = null
    private var mContentView: WearableRecyclerView? = null
    private var mSnapHelper: LinearSnapHelper? = null
    private var mSpinner: ProgressSpinner? = null

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.browser_view, container, false) ?: return null
        mContentView = view.findViewById(R.id.recycler_view) as WearableRecyclerView
        mContentView!!.layoutManager = CurvedChildLayoutManager(activity)
        mContentView!!.centerEdgeItems = true
        mSnapHelper = LinearSnapHelper()
        mSnapHelper!!.attachToRecyclerView(mContentView)
        mSpinner = view.findViewById(R.id.spinner) as ProgressSpinner
        mAdapter = BrowserRVAdapter<Clickable>(createViewHolderFactory())
        mContentView!!.adapter = mAdapter
        return view
    }

    @CallSuper
    override fun onDestroyView() {
        mSnapHelper!!.attachToRecyclerView(null)
        mContentView!!.adapter = null
        mAdapter = null
        super.onDestroyView()
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        val settingsManager = activity.settingsManager
        mContentView!!.isCircularScrollingGestureEnabled =
                settingsManager.useCircularScrollingGesture()
        settingsManager.addSettingsListener(mSettingsListener)
        activity.musicLibraryCache.addInvalidationListener(mCacheInvalidationListener)
        refresh(true)
    }

    fun refresh() {
        onGoingToRefresh()
        refresh(false)
        mContentView?.scrollToPosition(0)
    }

    private fun refresh(restoreScrollState: Boolean) {
        if (tryRestoreCachedItems()) {
            mSpinner!!.visibility = View.GONE
            mContentView!!.visibility = View.VISIBLE
            if (restoreScrollState) mScrollStateHelper.restoreTo(mContentView)
        } else {
            mSpinner!!.visibility = View.VISIBLE
            mContentView!!.visibility = View.GONE
            fetchItems()
        }
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        activity.settingsManager.removeListener(mSettingsListener)
        activity.musicLibraryCache.removeInvalidationListener(mCacheInvalidationListener)
        mScrollStateHelper.save(mContentView)
    }

    protected fun setItems(items: List<Clickable>) {
        mAdapter!!.items = items
        mSpinner!!.visibility = View.GONE
        mContentView!!.visibility = View.VISIBLE
        var scrollToPosition = 0
        val scrollDest = scrollDestination
        if (scrollDest !== null && !TextUtils.isEmpty(scrollDest)) {
            val count = items.size
            for (i in 0..count - 1) {
                if (shouldScrollTo(items[i], scrollDest)) {
                    scrollToPosition = i
                    break
                }
            }
        }
        val scrollToPosCopy = scrollToPosition
        mContentView!!.post { mContentView!!.scrollToPosition(scrollToPosCopy) }
    }

    private val scrollDestination: String? get() = arguments?.getString(SCROLL_DESTINATION_KEY)
}
