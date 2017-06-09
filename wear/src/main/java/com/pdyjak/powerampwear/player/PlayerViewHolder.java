package com.pdyjak.powerampwear.player;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdyjak.powerampwear.R;

class PlayerViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, PlayerViewModel.CommonEventsListener {

    private static final int PLAY_PAUSE_TAG = 0;
    private static final int PREV_SONG_TAG = 1;
    private static final int NEXT_SONG_TAG = 2;
    private static final int VOLUME_DOWN_TAG = 3;
    private static final int VOLUME_UP_TAG = 4;

    @NonNull
    private final PlayerViewModel mViewModel;
    @NonNull
    private final View mProgressSpinner;
    @NonNull
    private final View mErrorContainer;
    @NonNull
    private final View mPlayerViewRoot;
    @NonNull
    private final TextView mTitleTextView;
    @NonNull
    private final TextView mArtistAlbumTextView;
    @NonNull
    private final ImageView mPlayPauseButton;

    PlayerViewHolder(@NonNull View view, @NonNull PlayerViewModel viewModel) {
        super(view);
        mViewModel = viewModel;
        mViewModel.addListenerWeakly(this);
        mProgressSpinner = view.findViewById(R.id.progress_spinner);
        mErrorContainer = view.findViewById(R.id.error_container);
        mPlayerViewRoot = view.findViewById(R.id.player_root);

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

        onStateChanged();
        onPauseChanged();
        onTrackInfoChanged();
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
        }
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
        mTitleTextView.setText(mViewModel.getTitle());
        mArtistAlbumTextView.setText(mViewModel.getArtistAlbum());
    }
}
