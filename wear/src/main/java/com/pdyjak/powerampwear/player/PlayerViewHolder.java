package com.pdyjak.powerampwear.player;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.pdyjak.powerampwear.App;
import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwear.custom_views.CircularProgressbar;
import com.pdyjak.powerampwear.settings.SettingsManager;

class PlayerViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, PlayerViewModel.CommonEventsListener,
        PlayerViewModel.UiElementsVisibilityListener, PlayerViewModel.TrackPositionListener {

    private static final int PLAY_PAUSE_TAG = 0;
    private static final int PREV_SONG_TAG = 1;
    private static final int NEXT_SONG_TAG = 2;
    private static final int VOLUME_DOWN_TAG = 3;
    private static final int VOLUME_UP_TAG = 4;
    private static final int TRACK_INFO_TAG = 5;
    private static final int QUICK_NAV_HINT_TAG = 6;

    @NonNull
    private final PlayerViewModel mViewModel;
    @NonNull
    private final View mProgressSpinner;
    @NonNull
    private final View mErrorContainer;
    @NonNull
    private final View mPlayerViewRoot;
    @NonNull
    private final View mTrackInfoView;
    @NonNull
    private final TextView mTitleTextView;
    @NonNull
    private final TextView mArtistAlbumTextView;
    @NonNull
    private final ImageView mPlayPauseButton;
    @NonNull
    private final TextClock mClock;
    @NonNull
    private final CircularProgressbar mProgressbar;
    @NonNull
    private final View mQuickNavHint;
    private boolean mQuickNavHintVisible;

    PlayerViewHolder(@NonNull View view, @NonNull PlayerViewModel viewModel) {
        super(view);
        mViewModel = viewModel;
        mViewModel.addListenerWeakly((PlayerViewModel.CommonEventsListener) this);
        mViewModel.addListenerWeakly((PlayerViewModel.UiElementsVisibilityListener) this);
        mViewModel.setTrackPositionListenerWeakly(this);
        mProgressSpinner = view.findViewById(R.id.progress_spinner);
        mErrorContainer = view.findViewById(R.id.error_container);
        mPlayerViewRoot = view.findViewById(R.id.player_root);

        mTrackInfoView = view.findViewById(R.id.track_info);
        mTrackInfoView.setTag(TRACK_INFO_TAG);
        mTrackInfoView.setOnClickListener(this);

        mTitleTextView = (TextView) view.findViewById(R.id.title);
        mArtistAlbumTextView = (TextView) view.findViewById(R.id.artist_album);

        mPlayPauseButton = (ImageView) view.findViewById(R.id.play_pause_button);
        mPlayPauseButton.setTag(PLAY_PAUSE_TAG);
        mPlayPauseButton.setOnClickListener(this);

        View prevSong = view.findViewById(R.id.prev_song_button);
        prevSong.setTag(PREV_SONG_TAG);
        prevSong.setOnClickListener(this);

        View nextSong = view.findViewById(R.id.next_song_button);
        nextSong.setTag(NEXT_SONG_TAG);
        nextSong.setOnClickListener(this);

        View volumeDownButton = view.findViewById(R.id.volume_down_button);
        volumeDownButton.setTag(VOLUME_DOWN_TAG);
        volumeDownButton.setOnClickListener(this);

        View volumeUpButton = view.findViewById(R.id.volume_up_button);
        volumeUpButton.setTag(VOLUME_UP_TAG);
        volumeUpButton.setOnClickListener(this);

        mQuickNavHint = view.findViewById(R.id.quick_nav_tooltip);
        mQuickNavHint.setTag(QUICK_NAV_HINT_TAG);
        mQuickNavHint.setOnClickListener(this);

        mClock = (TextClock) view.findViewById(R.id.clock);
        mProgressbar = (CircularProgressbar) view.findViewById(R.id.seekbar);

        onStateChanged();
        onPauseChanged();
        onTrackInfoChanged(true);
        onClockVisibilityChanged();
        onProgressbarVisibilityChanged();
    }

    @Override
    public void onClick(View v) {
        switch ((int) v.getTag()) {
            case PLAY_PAUSE_TAG:
                mViewModel.togglePlayPause();
                break;

            case PREV_SONG_TAG:
                mViewModel.previousTrack();
                break;

            case NEXT_SONG_TAG:
                mViewModel.nextTrack();
                break;

            case VOLUME_DOWN_TAG:
                mViewModel.volumeDown();
                break;

            case VOLUME_UP_TAG:
                mViewModel.volumeUp();
                break;

            case TRACK_INFO_TAG:
                hideQuickNavigationHint();
                mViewModel.goToLibrary();
                break;

            case QUICK_NAV_HINT_TAG:
                hideQuickNavigationHint();
                break;
        }
    }

    private void hideQuickNavigationHint() {
        if (!mQuickNavHintVisible) return;
        SettingsManager settingsManager = ((App) itemView.getContext().getApplicationContext())
                .getSettingsManager();
        settingsManager.saveQuickNavigationHintShown();
        mQuickNavHintVisible = false;
        mQuickNavHint.setVisibility(View.GONE);
    }

    @Override
    public void onStateChanged() {
        switch (mViewModel.getCurrentState()) {
            case Loading:
                mProgressSpinner.setVisibility(View.VISIBLE);
                mErrorContainer.setVisibility(View.GONE);
                mPlayerViewRoot.setVisibility(View.GONE);
                break;

            case Failure:
                mProgressSpinner.setVisibility(View.GONE);
                mErrorContainer.setVisibility(View.VISIBLE);
                mPlayerViewRoot.setVisibility(View.GONE);
                break;

            case Ok:
                mProgressSpinner.setVisibility(View.GONE);
                mErrorContainer.setVisibility(View.GONE);
                mPlayerViewRoot.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onPauseChanged() {
        mPlayPauseButton.setImageResource(mViewModel.isPaused()
                ? R.drawable.ic_play_arrow_black_48dp
                : R.drawable.ic_pause_black_48dp);
    }

    @Override
    public void onTrackInfoChanged() {
        onTrackInfoChanged(false);
    }

    private void onTrackInfoChanged(boolean initial) {
        mTitleTextView.setText(mViewModel.getTitle());
        mArtistAlbumTextView.setText(mViewModel.getArtistAlbum());
        if (!initial) {
            showQuickNavigationHintIfNeeded();
        }
    }

    private void showQuickNavigationHintIfNeeded() {
        SettingsManager settingsManager = ((App) itemView.getContext().getApplicationContext())
                .getSettingsManager();
        if (settingsManager.quickNavigationHintShown()) return;
        if (mQuickNavHintVisible) return;
        mQuickNavHintVisible = true;
        ValueAnimator animator = ValueAnimator.ofFloat(0.35f, 1f);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mQuickNavHint.setScaleX(0.35f);
                mQuickNavHint.setScaleY(0.35f);
                mQuickNavHint.setAlpha(0.35f);
                mQuickNavHint.setVisibility(View.VISIBLE);
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                mQuickNavHint.setScaleX(progress);
                mQuickNavHint.setScaleY(progress);
                mQuickNavHint.setAlpha(progress);
            }
        });
        animator.start();
    }

    @Override
    public void onClockVisibilityChanged() {
        mClock.setVisibility(mViewModel.shouldShowClock() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onProgressbarVisibilityChanged() {
        mProgressbar.setVisibility(mViewModel.shouldShowProgressbar() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPositionChanged(int position, int duration) {
        if (duration == 0) return;
        mProgressbar.setProgress(Math.min(1.0f, (float) position / duration));
    }
}
