package com.pdyjak.powerampwear

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.view.ViewPager
import android.support.wearable.activity.WearableActivity
import android.view.View
import android.widget.ImageView

import com.google.android.gms.wearable.MessageEvent
import com.pdyjak.powerampwear.music_browser.albums.AlbumItem
import com.pdyjak.powerampwear.music_browser.categories.CategoryItem
import com.pdyjak.powerampwear.music_browser.files.FileItem
import com.pdyjak.powerampwear.music_browser.folders.FolderItem
import com.pdyjak.powerampwearcommon.events.AlbumArtChangedEvent
import com.pdyjak.powerampwearcommon.events.TrackChangedEvent

import me.relex.circleindicator.CircleIndicator

class MainActivity : WearableActivity(), MessageListener {

    companion object {
        const val DOTS_VISIBILITY_TIMEOUT: Long = 1000
        const val OFFSCREEN_PAGE_LIMIT = 2
    }

    private inner class MusicBrowserListener : MusicBrowserListenerAdapter() {
        override fun onFileSelected(item: FileItem, fromPlayer: Boolean) {
            mShouldNavigateToPlayerView = true
        }

        override fun onFolderSelected(item: FolderItem, fromPlayer: Boolean, scrollTo: String?) {
            goToMusicBrowserIfNeeded(fromPlayer)
        }

        override fun onAlbumSelected(item: AlbumItem, fromPlayer: Boolean, scrollTo: String?) {
            goToMusicBrowserIfNeeded(fromPlayer)
        }

        override fun onCategorySelected(item: CategoryItem, fromPlayer: Boolean,
                                        scrollTo: String?) {
            goToMusicBrowserIfNeeded(fromPlayer)
        }

        private fun goToMusicBrowserIfNeeded(fromPlayer: Boolean) {
            if (!fromPlayer) return
            mViewPager!!.setCurrentItem(1, true)
        }
    }

    private inner class PageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float,
                                    positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {}

        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (mHideDotsIndicatorRunnable === null) {
                    mHideDotsIndicatorRunnable = Runnable {
                        mDotsIndicator!!.visibility = View.GONE
                    }
                    mHandler.postDelayed(mHideDotsIndicatorRunnable, DOTS_VISIBILITY_TIMEOUT)
                }
            } else {
                if (mHideDotsIndicatorRunnable !== null) {
                    mHandler.removeCallbacks(mHideDotsIndicatorRunnable)
                    mHideDotsIndicatorRunnable = null
                }
                mDotsIndicator!!.visibility = View.VISIBLE
            }
        }
    }

    private val mHandler = Handler(Looper.getMainLooper())
    private val mMusicBrowserListener = MusicBrowserListener()

    private var mAlbumArt: ImageView? = null
    private var mViewPager: ViewPager? = null
    private var mDotsIndicator: CircleIndicator? = null
    private var mAlbumArtBitmap: Bitmap? = null
    private var mShouldNavigateToPlayerView: Boolean = false
    private var mHideDotsIndicatorRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAmbientEnabled()
        setContentView(R.layout.activity_main)
        mViewPager = findViewById(R.id.view_pager) as ViewPager
        mViewPager!!.adapter = MainViewPagerAdapter(fragmentManager)
        mViewPager!!.addOnPageChangeListener(PageChangeListener())
        mViewPager!!.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
        mAlbumArt = findViewById(R.id.album_art) as ImageView
        mDotsIndicator = findViewById(R.id.dots_indicator) as CircleIndicator
        mDotsIndicator!!.setViewPager(mViewPager)
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        ambientModeStateProviderInternal.isInAmbientMode = true
        updateDisplay()
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
        ambientModeStateProviderInternal.isInAmbientMode = false
        updateDisplay()
    }

    override fun onResume() {
        app.onResume()
        app.messageExchangeHelper.addMessageListenerWeakly(this)
        app.musicLibraryNavigator.addLibraryNavigationListener(mMusicBrowserListener)
        super.onResume()
    }

    override fun onPause() {
        app.onPause()
        app.messageExchangeHelper.removeMessageListener(this)
        app.musicLibraryNavigator.removeLibraryNavigationListener(mMusicBrowserListener)
        super.onPause()
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (AlbumArtChangedEvent.PATH == messageEvent.path) {
            val bytes = messageEvent.data
            if (bytes === null) {
                mAlbumArtBitmap = null
            } else {
                mAlbumArtBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
            updateDisplay()
        } else if (TrackChangedEvent.PATH == messageEvent.path && mShouldNavigateToPlayerView) {
            mShouldNavigateToPlayerView = false
            mViewPager!!.setCurrentItem(0, true)
        }
    }

    private fun updateDisplay() {
        mAlbumArt!!.setImageBitmap(if (isAmbient) null else mAlbumArtBitmap)
    }

}
