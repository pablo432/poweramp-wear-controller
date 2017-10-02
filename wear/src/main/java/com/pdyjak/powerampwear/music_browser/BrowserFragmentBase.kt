@file:Suppress("DEPRECATION") // for ProgressSpinner

package com.pdyjak.powerampwear.music_browser

import android.app.Fragment
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.LinearSnapHelper
import android.support.wearable.view.CurvedChildLayoutManager
import android.support.wearable.view.ProgressSpinner
import android.support.wearable.view.WearableRecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.common.EventArgs
import com.pdyjak.powerampwear.common.byId
import com.pdyjak.powerampwear.common.musicLibraryCache
import com.pdyjak.powerampwear.common.nullIfEmpty
import com.pdyjak.powerampwear.common.settingsManager
import com.pdyjak.powerampwear.settings.SettingsManager

abstract class BrowserFragmentBase : Fragment() {

    companion object {
        const val SCROLL_DESTINATION_KEY = "scroll_to"
    }

    private inner class SettingsListener : SettingsManager.Listener() {
        override fun onCircularScrollingChanged() {
            mViews?.updateCircullarScrolling()
        }
    }

    private val mCacheInvalidationListener = { _: EventArgs? ->
        mViews?.loading = true
        fetchItems()
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

    private inner class Views(view: View) {
        private val mContentView: WearableRecyclerView = view byId R.id.recycler_view
        private val mSpinner: ProgressSpinner = view byId R.id.spinner
        private val mSnapHelper = LinearSnapHelper()
        private val mAdapter = BrowserRVAdapter<Clickable>(createViewHolderFactory())

        var loading: Boolean = false
            get
            set(value) {
                if (field == value) return
                if (value) {
                    mSpinner.visibility = View.VISIBLE
                    mContentView.visibility = View.GONE
                } else {
                    mSpinner.visibility = View.GONE
                    mContentView.visibility = View.VISIBLE
                }
            }

        init {
            mContentView.layoutManager = CurvedChildLayoutManager(activity)
            mContentView.centerEdgeItems = true
            mContentView.adapter = mAdapter
            mSnapHelper.attachToRecyclerView(mContentView)
        }

        fun resume() {
            refresh(true)
        }

        fun pause() {
            mScrollStateHelper.save(mContentView)
        }

        fun destroy() {
            mSnapHelper.attachToRecyclerView(null)
            mContentView.adapter = null
            mContentView.layoutManager = null
        }

        fun refresh(restoreScrollState: Boolean) {
            if (tryRestoreCachedItems()) {
                loading = false
                if (restoreScrollState) mScrollStateHelper.restoreTo(mContentView)
            } else {
                loading = true
                fetchItems()
            }
            if (!restoreScrollState) mContentView.scrollToPosition(0)
        }

        fun setAdapterItems(items: List<Clickable>) {
            mAdapter.items = items
            mSpinner.visibility = View.GONE
            mContentView.visibility = View.VISIBLE
            var scrollToPosition = 0
            scrollDestination?.let {
                scrollToPosition = items.withIndex()
                        .find({ pair -> shouldScrollTo(pair.value, it) })?.index ?: 0
            }
            mContentView.post { mContentView.scrollToPosition(scrollToPosition) }
        }

        fun updateCircullarScrolling() {
            mContentView.isCircularScrollingGestureEnabled =
                    activity.settingsManager.useCircularScrollingGesture()
        }
    }

    private val mScrollStateHelper = ScrollStateHelper()
    private val mSettingsListener = SettingsListener()
    private var mViews: Views? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.browser_view, container, false)
        mViews = Views(view)
        return view
    }

    @CallSuper
    override fun onDestroyView() {
        mViews!!.destroy()
        mViews = null
        super.onDestroyView()
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        val settingsManager = activity.settingsManager
        settingsManager.addSettingsListener(mSettingsListener)
        activity.musicLibraryCache.onInvalidation += mCacheInvalidationListener
        mViews!!.resume()
    }

    fun refresh() {
        onGoingToRefresh()
        mViews?.refresh(false)
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        activity.settingsManager.removeListener(mSettingsListener)
        activity.musicLibraryCache.onInvalidation -= mCacheInvalidationListener
        mViews!!.pause()
    }

    protected fun setItems(items: List<Clickable>) {
        mViews!!.setAdapterItems(items)
    }

    private val scrollDestination: String? get() = arguments?.getString(SCROLL_DESTINATION_KEY)
            ?.nullIfEmpty()
}
