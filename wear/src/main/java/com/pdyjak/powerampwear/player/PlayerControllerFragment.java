package com.pdyjak.powerampwear.player;

import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageEvent;
import com.maxmpz.poweramp.player.PowerampAPI;
import com.pdyjak.powerampwear.App;
import com.pdyjak.powerampwear.MessageExchangeHelper;
import com.pdyjak.powerampwear.MessageListener;
import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwearcommon.events.PlayingModeChangedEvent;
import com.pdyjak.powerampwearcommon.events.StatusChangedEvent;
import com.pdyjak.powerampwearcommon.events.TrackChangedEvent;
import com.pdyjak.powerampwearcommon.requests.RequestsPaths;

public class PlayerControllerFragment extends android.app.Fragment
        implements View.OnClickListener, MessageListener {

    private static final int DEFAULT_TIMEOUT = 5000;

    private static final int REPEAT_MAX_LEVEL = 3;
    private static final int SHUFFLE_MAX_LEVEL = 4;

    private static final int PLAY_PAUSE_TAG = 0;
    private static final int PREV_SONG_TAG = 1;
    private static final int NEXT_SONG_TAG = 2;
    private static final int REPEAT_TAG = 3;
    private static final int SHUFFLE_TAG = 4;

    private enum ViewState {
        Loading,
        Failure,
        Ok
    }

    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    @NonNull
    private final Runnable mTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            updateVisualState(ViewState.Failure);
        }
    };

    private View mProgressSpinner;
    private View mErrorContainer;
    private View mPlayerViewRoot;

    private TextView mTitleTextView;
    private TextView mArtistAlbumTextView;
    private ImageView mPlayPauseButton;
    private LevelListDrawable mRepeatButtonDrawable;
    private LevelListDrawable mShuffleButtonDrawable;
    private boolean mPaused;
    private ViewState mCurrentViewState;
    private boolean mTimeoutRunnablePosted;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_view, container, false);
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

        ImageView repeatButton = (ImageView) view.findViewById(R.id.repeat_button);
        repeatButton.setTag(REPEAT_TAG);
        repeatButton.setOnClickListener(this);
        mRepeatButtonDrawable = (LevelListDrawable) repeatButton.getDrawable();

        ImageView shuffleButton = (ImageView) view.findViewById(R.id.shuffle_button);
        shuffleButton.setTag(SHUFFLE_TAG);
        shuffleButton.setOnClickListener(this);
        mShuffleButtonDrawable = (LevelListDrawable) shuffleButton.getDrawable();

        updateVisualState(ViewState.Loading);
        if (!mTimeoutRunnablePosted) {
            mTimeoutRunnablePosted = true;
            mHandler.postDelayed(mTimeoutRunnable, DEFAULT_TIMEOUT);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessageExchangeHelper().addMessageListenerWeakly(this);
        getMessageExchangeHelper().sendRequest(RequestsPaths.REFRESH_TRACK_INFO);
    }

    private MessageExchangeHelper getMessageExchangeHelper() {
        return ((App) getActivity().getApplicationContext()).getMessageExchangeHelper();
    }

    @Override
    public void onPause() {
        super.onPause();
        getMessageExchangeHelper().removeMessageListener(this);
        removeTimeoutCallbackFromHandler();
    }

    private void removeTimeoutCallbackFromHandler() {
        if (mTimeoutRunnablePosted) {
            mTimeoutRunnablePosted = false;
            mHandler.removeCallbacks(mTimeoutRunnable);
        }
    }

    @Override
    public void onClick(View v) {
        MessageExchangeHelper helper = getMessageExchangeHelper();
        switch ((int) v.getTag()) {
            case PLAY_PAUSE_TAG:
                // Fake the change to improve responsibility, then make sure it's ok when
                // message is received.
                mPaused = !mPaused;
                updatePlayPauseButtonState();
                helper.sendRequest(RequestsPaths.TOGGLE_PLAY_PAUSE);
                break;

            case PREV_SONG_TAG:
                helper.sendRequest(RequestsPaths.PREV_TRACK);
                break;

            case NEXT_SONG_TAG:
                helper.sendRequest(RequestsPaths.NEXT_TRACK);
                break;

            case REPEAT_TAG:
                onRepeatClicked();
                break;

            case SHUFFLE_TAG:
                onShuffleClicked();
                break;
        }
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        switch (messageEvent.getPath()) {
            case TrackChangedEvent.PATH:
                removeTimeoutCallbackFromHandler();
                updateVisualState(ViewState.Ok);
                processTrackInfo(TrackChangedEvent.fromBytes(messageEvent.getData()));
                break;

            case StatusChangedEvent.PATH:
                processStatusInfo(StatusChangedEvent.fromBytes(messageEvent.getData()));
                break;

            case PlayingModeChangedEvent.PATH:
                processPlayingModeInfo(PlayingModeChangedEvent.fromBytes(messageEvent.getData()));
                break;
        }
    }

    private void onRepeatClicked() {
        int current = mRepeatButtonDrawable.getLevel();
        mRepeatButtonDrawable.setLevel(current == REPEAT_MAX_LEVEL ? 0 : current + 1);
        getMessageExchangeHelper().sendRequest(RequestsPaths.TOGGLE_REPEAT_MODE);
    }

    private void onShuffleClicked() {
        int current = mShuffleButtonDrawable.getLevel();
        mShuffleButtonDrawable.setLevel(current == SHUFFLE_MAX_LEVEL ? 0 : current + 1);
        getMessageExchangeHelper().sendRequest(RequestsPaths.TOGGLE_SHUFFLE_MODE);
    }

    private void processPlayingModeInfo(@NonNull PlayingModeChangedEvent playingModeChangedEvent) {
        mShuffleButtonDrawable.setLevel(playingModeChangedEvent.shuffleMode);
        mRepeatButtonDrawable.setLevel(playingModeChangedEvent.repeatMode);
    }

    private void processTrackInfo(@NonNull TrackChangedEvent event) {
        mTitleTextView.setText(event.title);
        String secondLine = "";
        if (event.artist != null) {
            secondLine = event.artist;
        }
        if (event.album != null) {
            if (secondLine.length() > 0) secondLine += " - ";
            secondLine += event.album;
        }
        mArtistAlbumTextView.setText(secondLine);
    }

    private void processStatusInfo(@NonNull StatusChangedEvent event) {
        mPaused = event.status != PowerampAPI.Status.TRACK_PLAYING || event.paused;
        updatePlayPauseButtonState();
    }

    private void updatePlayPauseButtonState() {
        mPlayPauseButton.setImageResource(mPaused ? R.drawable.ic_play_arrow_black_48dp
                : R.drawable.ic_pause_black_48dp);
    }

    private void updateVisualState(@NonNull ViewState state) {
        if (state == mCurrentViewState) return;
        mCurrentViewState = state;
        switch (state) {
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
}
