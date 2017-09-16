package com.pdyjak.powerampwear.player

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextClock
import android.widget.TextView

import com.pdyjak.powerampwear.App
import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.custom_views.CircularProgressbar
import com.pdyjak.powerampwear.settingsManager

internal class PlayerViewHolder(view: View, private val mViewModel: PlayerViewModel)
    : RecyclerView.ViewHolder(view), PlayerViewModel.CommonEventsListener,
        PlayerViewModel.UiElementsVisibilityListener, PlayerViewModel.TrackPositionListener {

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
        mViewModel.addListenerWeakly(this as PlayerViewModel.CommonEventsListener)
        mViewModel.addListenerWeakly(this as PlayerViewModel.UiElementsVisibilityListener)
        mViewModel.setTrackPositionListenerWeakly(this)
        mProgressSpinner = view.findViewById(R.id.progress_spinner)
        mErrorContainer = view.findViewById(R.id.error_container)
        mPlayerViewRoot = view.findViewById(R.id.player_root)

        mTrackInfoView = view.findViewById(R.id.track_info)
        mTrackInfoView.setOnClickListener {
            hideQuickNavigationHint()
            mViewModel.goToLibrary()
        }

        mTitleTextView = view.findViewById(R.id.title) as TextView
        mArtistAlbumTextView = view.findViewById(R.id.artist_album) as TextView

        mPlayPauseButton = view.findViewById(R.id.play_pause_button) as ImageView
        mPlayPauseButton.setOnClickListener { mViewModel.togglePlayPause() }

        val prevSong = view.findViewById(R.id.prev_song_button)
        prevSong.setOnClickListener { mViewModel.previousTrack() }

        val nextSong = view.findViewById(R.id.next_song_button)
        nextSong.setOnClickListener { mViewModel.nextTrack() }

        val volumeDownButton = view.findViewById(R.id.volume_down_button)
        volumeDownButton.setOnClickListener { mViewModel.volumeDown() }

        val volumeUpButton = view.findViewById(R.id.volume_up_button)
        volumeUpButton.setOnClickListener { mViewModel.volumeUp() }

        mQuickNavHint = view.findViewById(R.id.quick_nav_tooltip)
        mQuickNavHint.setOnClickListener { hideQuickNavigationHint() }

        mClock = view.findViewById(R.id.clock) as TextClock
        mProgressbar = view.findViewById(R.id.seekbar) as CircularProgressbar

        onStateChanged()
        onPauseChanged()
        onTrackInfoChanged(true)
        onClockVisibilityChanged()
        onProgressbarVisibilityChanged()
    }

    private fun hideQuickNavigationHint() {
        if (!mQuickNavHintVisible) return
        val settingsManager = itemView.context.settingsManager
        settingsManager.saveQuickNavigationHintShown()
        mQuickNavHintVisible = false
        mQuickNavHint.visibility = View.GONE
    }

    override fun onStateChanged() {
        when (mViewModel.currentState) {
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

    override fun onPauseChanged() {
        mPlayPauseButton.setImageResource(if (mViewModel.isPaused)
            R.drawable.ic_play_arrow_black_48dp
        else
            R.drawable.ic_pause_black_48dp)
    }

    override fun onTrackInfoChanged() {
        onTrackInfoChanged(false)
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

    override fun onClockVisibilityChanged() {
        mClock.visibility = if (mViewModel.shouldShowClock()) View.VISIBLE else View.GONE
    }

    override fun onProgressbarVisibilityChanged() {
        mProgressbar.visibility = if (mViewModel.shouldShowProgressbar())
            View.VISIBLE else View.GONE
    }

    override fun onPositionChanged(position: Int, duration: Int) {
        if (duration == 0) return
        mProgressbar.setProgress(Math.min(1.0f, position.toFloat() / duration))
    }
}
