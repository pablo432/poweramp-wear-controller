package com.pdyjak.powerampwear.player;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.wearable.MessageEvent;
import com.maxmpz.poweramp.player.PowerampAPI;
import com.pdyjak.powerampwear.MessageExchangeHelper;
import com.pdyjak.powerampwear.MessageListener;
import com.pdyjak.powerampwearcommon.events.PlayingModeChangedEvent;
import com.pdyjak.powerampwearcommon.events.StatusChangedEvent;
import com.pdyjak.powerampwearcommon.events.TrackChangedEvent;
import com.pdyjak.powerampwearcommon.requests.RequestsPaths;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

class PlayerViewModel implements MessageListener {
    private static final int DEFAULT_TIMEOUT = 5000;

    private static final int REPEAT_MAX_LEVEL = 3;
    private static final int SHUFFLE_MAX_LEVEL = 4;

    interface CommonEventsListener {
        void onStateChanged();
        void onPauseChanged();
        void onTrackInfoChanged();
    }

    interface RepeatShuffleModesListener {
        void onRepeatModeChanged();
        void onShuffleModeChanged();
    }

    enum State {
        Loading,
        Failure,
        Ok
    }

    enum ShuffleMode {
        Off(0),
        All(1),
        Songs(2),
        Lists(3),
        SongsLists(4);

        final int value;
        ShuffleMode(int value) {
            this.value = value;
        }

        @Nullable
        static ShuffleMode from(int value) {
            ShuffleMode modes[] = ShuffleMode.values();
            if (value < modes.length) return modes[value];
            return null;
        }
    }

    enum RepeatMode {
        Off(0),
        List(1),
        AdvanceList(2),
        Song(3);

        final int value;
        RepeatMode(int value) {
            this.value = value;
        }

        @Nullable
        static RepeatMode from(int value) {
            RepeatMode modes[] = RepeatMode.values();
            if (value < modes.length) return modes[value];
            return null;
        }
    }

    @NonNull
    private final MessageExchangeHelper mMessageExchangeHelper;
    @NonNull
    private final Set<CommonEventsListener> mListeners =
            Collections.newSetFromMap(new WeakHashMap<CommonEventsListener, Boolean>());
    @NonNull
    private final Set<RepeatShuffleModesListener> mRepeatShuffleListeners =
            Collections.newSetFromMap(new WeakHashMap<RepeatShuffleModesListener, Boolean>());
    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    @NonNull
    private final Runnable mTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            setState(State.Failure);
        }
    };

    private boolean mPaused = true;
    private boolean mTimeoutRunnablePosted;
    @NonNull
    private State mCurrentState = State.Loading;
    @NonNull
    private RepeatMode mRepeatMode = RepeatMode.Off;
    @NonNull
    private ShuffleMode mShuffleMode = ShuffleMode.Off;
    @Nullable
    private String mTitle;
    @Nullable
    private String mArtistAlbum;

    PlayerViewModel(@NonNull MessageExchangeHelper helper) {
        mMessageExchangeHelper = helper;
    }

    void addListenerWeakly(@NonNull CommonEventsListener listener) {
        mListeners.add(listener);
    }

    void addListenerWeakly(@NonNull RepeatShuffleModesListener listener) {
        mRepeatShuffleListeners.add(listener);
    }

    void removeListener(@NonNull CommonEventsListener listener) {
        mListeners.remove(listener);
    }

    void removeListener(@NonNull RepeatShuffleModesListener listener) {
        mRepeatShuffleListeners.remove(listener);
    }

    void onResume() {
        if (!mTimeoutRunnablePosted) {
            mTimeoutRunnablePosted = true;
            mHandler.postDelayed(mTimeoutRunnable, DEFAULT_TIMEOUT);
        }
        mMessageExchangeHelper.addMessageListenerWeakly(this);
        mMessageExchangeHelper.sendRequest(RequestsPaths.REFRESH_TRACK_INFO);
    }

    void onPause() {
        mMessageExchangeHelper.removeMessageListener(this);
        removeTimeoutCallbackFromHandler();
    }

    @NonNull
    State getCurrentState() {
        return mCurrentState;
    }

    boolean isPaused() {
        return mPaused;
    }

    @NonNull
    RepeatMode getRepeatMode() {
        return mRepeatMode;
    }

    @NonNull
    ShuffleMode getShuffleMode() {
        return mShuffleMode;
    }

    @Nullable
    String getTitle() {
        return mTitle;
    }

    @Nullable
    String getArtistAlbum() {
        return mArtistAlbum;
    }

    private void removeTimeoutCallbackFromHandler() {
        if (mTimeoutRunnablePosted) {
            mTimeoutRunnablePosted = false;
            mHandler.removeCallbacks(mTimeoutRunnable);
        }
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        switch (messageEvent.getPath()) {
            case TrackChangedEvent.PATH:
                removeTimeoutCallbackFromHandler();
                setState(State.Ok);
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

    private void processPlayingModeInfo(@NonNull PlayingModeChangedEvent playingModeChangedEvent) {
        ShuffleMode shuffleMode = ShuffleMode.from(playingModeChangedEvent.shuffleMode);
        if (shuffleMode != null) setShuffleMode(shuffleMode);
        RepeatMode repeatMode = RepeatMode.from(playingModeChangedEvent.repeatMode);
        if (repeatMode != null) setRepeatMode(repeatMode);
    }

    private void processTrackInfo(@NonNull TrackChangedEvent event) {
        boolean changed = setTitle(event.title);
        String secondLine = "";
        if (event.artist != null) {
            secondLine = event.artist;
        }
        if (event.album != null) {
            if (secondLine.length() > 0) secondLine += " - ";
            secondLine += event.album;
        }
        changed |= setArtistAlbum(secondLine);
        if (changed) {
            Set<CommonEventsListener> copy = new HashSet<>(mListeners);
            for (CommonEventsListener listener : copy) {
                listener.onTrackInfoChanged();
            }
        }
    }

    private void processStatusInfo(@NonNull StatusChangedEvent event) {
        setPaused(event.status != PowerampAPI.Status.TRACK_PLAYING || event.paused);
    }

    void togglePlayPause() {
        setPaused(!mPaused);
        mMessageExchangeHelper.sendRequest(RequestsPaths.TOGGLE_PLAY_PAUSE);
    }

    void toggleRepeatMode() {
        RepeatMode next = mRepeatMode.value == REPEAT_MAX_LEVEL ? RepeatMode.from(0)
                : RepeatMode.from(mRepeatMode.value + 1);
        if (next == null) return;
        setRepeatMode(next);
        mMessageExchangeHelper.sendRequest(RequestsPaths.TOGGLE_REPEAT_MODE);
    }

    void toggleShuffleMode() {
        ShuffleMode next = mShuffleMode.value == SHUFFLE_MAX_LEVEL ? ShuffleMode.from(0)
                : ShuffleMode.from(mShuffleMode.value + 1);
        if (next == null) return;
        setShuffleMode(next);
        mMessageExchangeHelper.sendRequest(RequestsPaths.TOGGLE_SHUFFLE_MODE);
    }

    void previousTrack() {
        mMessageExchangeHelper.sendRequest(RequestsPaths.PREV_TRACK);
    }

    void nextTrack() {
        mMessageExchangeHelper.sendRequest(RequestsPaths.NEXT_TRACK);
    }

    void volumeDown() {
        mMessageExchangeHelper.sendRequest(RequestsPaths.VOLUME_DOWN);
    }

    void volumeUp() {
        mMessageExchangeHelper.sendRequest(RequestsPaths.VOLUME_UP);
    }

    private void setState(@NonNull State state) {
        if (mCurrentState == state) return;
        mCurrentState = state;
        Set<CommonEventsListener> copy = new HashSet<>(mListeners);
        for (CommonEventsListener listener : copy) {
            listener.onStateChanged();
        }
    }

    private void setPaused(boolean paused) {
        if (mPaused == paused) return;
        mPaused = paused;
        Set<CommonEventsListener> copy = new HashSet<>(mListeners);
        for (CommonEventsListener listener : copy) {
            listener.onPauseChanged();
        }
    }

    private void setRepeatMode(@NonNull RepeatMode mode) {
        if (mRepeatMode == mode) return;
        mRepeatMode = mode;
        Set<RepeatShuffleModesListener> copy = new HashSet<>(mRepeatShuffleListeners);
        for (RepeatShuffleModesListener listener : copy) {
            listener.onRepeatModeChanged();
        }
    }

    private void setShuffleMode(@NonNull ShuffleMode mode) {
        if (mShuffleMode == mode) return;
        mShuffleMode = mode;
        Set<RepeatShuffleModesListener> copy = new HashSet<>(mRepeatShuffleListeners);
        for (RepeatShuffleModesListener listener : copy) {
            listener.onShuffleModeChanged();
        }
    }

    private boolean setTitle(@Nullable String title) {
        if (equals(title, mTitle)) return false;
        mTitle = title;
        return true;
    }

    private boolean setArtistAlbum(@NonNull String artistAlbum) {
        if (artistAlbum.equals(mArtistAlbum)) return false;
        mArtistAlbum = artistAlbum;
        return true;
    }

    private static boolean equals(@Nullable Object o1, @Nullable Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }
}
