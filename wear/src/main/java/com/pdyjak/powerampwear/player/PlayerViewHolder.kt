package com.pdyjak.powerampwear.player

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextClock
import android.widget.TextView

import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.common.byId
import com.pdyjak.powerampwear.common.settingsManager
import com.pdyjak.powerampwear.custom_views.CircularProgressbar

internal class PlayerViewHolder(view: View, private val mViewModel: PlayerViewModel)
    : RecyclerView.ViewHolder(view) {

    private companion object {
        fun Boolean.toVisibility() = if (this) View.VISIBLE else View.GONE
    }

    private val mClockVisibilityHandler = {
        mClock.visibility = mViewModel.shouldShowClock.toVisibility()
    }

    private val mProgressbarVisibilityHandler = {
        mProgressbar.visibility = mViewModel.shouldShowProgressbar.toVisibility()
    }

    private val mStateChangedHandler = {
        when (mViewModel.state) {
            PlayerViewModel.State.Loading -> {
                mProgressSpinner.visibility = View.VISIBLE
                mErrorContainer.visibility = View.GONE
                mPlayerViewRoot.visibility = View.GONE
            }

            PlayerViewModel.State.Failure -> {
                mProgressSpinner.visibility = View.GONE
                mErrorContainer.visibility = View.VISIBLE
                mPlayerViewRoot.visibility = View.GONE
            }

            PlayerViewModel.State.Ok -> {
                mProgressSpinner.visibility = View.GONE
                mErrorContainer.visibility = View.GONE
                mPlayerViewRoot.visibility = View.VISIBLE
            }
        }
    }

    private val mPlayPauseChangedEventHandler = {
        mPlayPauseButton.setImageResource(if (mViewModel.paused)
            R.drawable.ic_play_arrow_black_48dp
        else
            R.drawable.ic_pause_black_48dp)
    }

    private val mTrackChangedInfoEventHandler = { onTrackInfoChanged(false) }

    private val mPositionChangedHandler = { (position, duration): PositionChangedEventArgs ->
        if (duration != 0) {
            mProgressbar.setProgress(Math.min(1.0f, position.toFloat() / duration))
        }
    }

    private val mProgressSpinner: View
    private val mErrorContainer: View
    private val mPlayerViewRoot: View
    private val mTrackInfoView: View
    private val mTitleTextView: TextView
    private val mArtistAlbumTextView: TextView
    private val mPlayPauseButton: ImageView
    private val mClock: TextClock
    private val mProgressbar: CircularProgressbar
    private val mQuickNavHint: View
    private var mQuickNavHintVisible: Boolean = false

    init {
        mViewModel.onClockVisibilityChanged.weakly() += mClockVisibilityHandler
        mViewModel.onProgressbarVisibilityChanged.weakly() += mProgressbarVisibilityHandler
        mViewModel.onStateChanged.weakly() += mStateChangedHandler
        mViewModel.onPauseChanged.weakly() += mPlayPauseChangedEventHandler
        mViewModel.onTrackInfoChanged.weakly() += mTrackChangedInfoEventHandler
        mViewModel.onPositionChanged.weakly() += mPositionChangedHandler
        mProgressSpinner = view byId  R.id.progress_spinner
        mErrorContainer = view byId R.id.error_container
        mPlayerViewRoot = view byId R.id.player_root

        mTrackInfoView = view byId R.id.track_info
        mTrackInfoView.setOnClickListener {
            hideQuickNavigationHint()
            mViewModel.goToLibrary()
        }

        mTitleTextView = view byId R.id.title
        mArtistAlbumTextView = view byId R.id.artist_album

        mPlayPauseButton = view byId R.id.play_pause_button
        mPlayPauseButton.setOnClickListener { mViewModel.togglePlayPause() }

        val prevSong = view.findViewById(R.id.prev_song_button)
        prevSong.setOnClickListener { mViewModel.previousTrack() }

        val nextSong = view.findViewById(R.id.next_song_button)
        nextSong.setOnClickListener { mViewModel.nextTrack() }

        val volumeDownButton = view.findViewById(R.id.volume_down_button)
        volumeDownButton.setOnClickListener { mViewModel.volumeDown() }

        val volumeUpButton = view.findViewById(R.id.volume_up_button)
        volumeUpButton.setOnClickListener { mViewModel.volumeUp() }

        mQuickNavHint = view byId R.id.quick_nav_tooltip
        mQuickNavHint.setOnClickListener { hideQuickNavigationHint() }

        mClock = view byId R.id.clock
        mProgressbar = view byId R.id.seekbar

        mStateChangedHandler()
        mPlayPauseChangedEventHandler()
        onTrackInfoChanged(true)
        mClockVisibilityHandler()
        mProgressbarVisibilityHandler()
    }

    private fun hideQuickNavigationHint() {
        if (!mQuickNavHintVisible) return
        val settingsManager = itemView.context.settingsManager
        settingsManager.saveQuickNavigationHintShown()
        mQuickNavHintVisible = false
        mQuickNavHint.visibility = View.GONE
    }

    private fun onTrackInfoChanged(initial: Boolean) {
        mTitleTextView.text = mViewModel.title
        mArtistAlbumTextView.text = mViewModel.artistAlbum
        if (!initial) showQuickNavigationHintIfNeeded()
    }

    private fun showQuickNavigationHintIfNeeded() {
        val settingsManager = itemView.context.settingsManager
        if (settingsManager.quickNavigationHintShown()) return
        if (mQuickNavHintVisible) return
        mQuickNavHintVisible = true
        val animator = ValueAnimator.ofFloat(0.35f, 1f)
        animator.duration = 300
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                mQuickNavHint.scaleX = 0.35f
                mQuickNavHint.scaleY = 0.35f
                mQuickNavHint.alpha = 0.35f
                mQuickNavHint.visibility = View.VISIBLE
            }
        })
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            mQuickNavHint.scaleX = progress
            mQuickNavHint.scaleY = progress
            mQuickNavHint.alpha = progress
        }
        animator.start()
    }

}
