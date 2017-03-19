package com.pdyjak.powerampwear;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.FirebaseApp;
import com.maxmpz.poweramp.player.PowerampAPI;
import com.pdyjak.powerampwearcommon.ConnectionState;
import com.pdyjak.powerampwearcommon.Message;
import com.pdyjak.powerampwearcommon.NodesResolver;
import com.pdyjak.powerampwearcommon.events.AlbumArtChangedEvent;
import com.pdyjak.powerampwearcommon.events.PlayingModeChangedEvent;
import com.pdyjak.powerampwearcommon.events.StatusChangedEvent;
import com.pdyjak.powerampwearcommon.events.TrackChangedEvent;
import com.pdyjak.powerampwearcommon.requests.GetAlbumsRequest;
import com.pdyjak.powerampwearcommon.requests.GetFilesRequest;
import com.pdyjak.powerampwearcommon.requests.PlaySongRequest;
import com.pdyjak.powerampwearcommon.requests.RequestsPaths;
import com.pdyjak.powerampwearcommon.responses.AlbumsResponse;
import com.pdyjak.powerampwearcommon.responses.ArtistsResponse;
import com.pdyjak.powerampwearcommon.responses.FilesListResponse;
import com.pdyjak.powerampwearcommon.responses.FoldersListResponse;
import com.pdyjak.powerampwearcommon.responses.Parent;

import java.io.ByteArrayOutputStream;
import java.util.Set;

/**
 * 1. Receives PowerAmp events and sends them to the Wearable device
 * 2. Handles requests from wearable device, like toggle play/pause, switching between songs and
 *    providing lists of songs.
 */
public class BackgroundService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private static final String PROCESS_POWERAMP_EVENTS_CAPABILITY_NAME = "process_poweramp_events";
    private static final int WAKE_LOCK_TIMEOUT = 2000;

    private Intent mTrackIntent;
    private Intent mStatusIntent;
    private Intent mAlbumArtIntent;
    private Intent mPlayingModeIntent;
    private Intent mRepeatModeIntent;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionState mConnectionState;
    private NodesResolver mNodesResolver;
    private PowerManager.WakeLock mWakeLock;
    private TrackChangedEvent mPreviousEvent;
    private TracksProvider mTracksProvider;
    private boolean mReceiversRegistered;

    @NonNull
    private final BroadcastReceiver mTrackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mTrackIntent = intent;
            processTrackIntent(false);
        }
    };

    @NonNull
    private final BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mStatusIntent = intent;
            processStatusIntent();
        }
    };

    @NonNull
    private final BroadcastReceiver mAlbumArtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAlbumArtIntent = intent;
            processAlbumArtIntent();
        }
    };

    @NonNull
    private final BroadcastReceiver mPlayingModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPlayingModeIntent = intent;
            processPlayingModeIntent();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.ENABLE_CRASH_REPORTING) FirebaseApp.initializeApp(this);
        mTracksProvider = new TracksProvider(getContentResolver());
        mGoogleApiClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(Wearable.API)
                .build();
        mConnectionState = ConnectionState.Connecting;
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHind) {
        mConnectionState = ConnectionState.Connected;
        mNodesResolver = new NodesResolver(mGoogleApiClient,
                PROCESS_POWERAMP_EVENTS_CAPABILITY_NAME);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        registerReceivers();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mConnectionState = ConnectionState.Suspended;
        unregisterReceivers();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mConnectionState = ConnectionState.FailedToConnect;
    }

    /*
     * Returns true, if an attempt to connect has been made (meaning that any functions which want
     * to send data to wearable device, should
     */
    private boolean connectIfNeeded() {
        if (mConnectionState == null || mConnectionState == ConnectionState.FailedToConnect) {
            mConnectionState = ConnectionState.Connecting;
            mGoogleApiClient.connect();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
        mConnectionState = null;
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        if (mNodesResolver != null) mNodesResolver.dispose();
        mGoogleApiClient.disconnect();
    }

    private void registerReceivers() {
        mReceiversRegistered = true;
        mTrackIntent = registerReceiver(mTrackReceiver,
                new IntentFilter(PowerampAPI.ACTION_TRACK_CHANGED));
        mStatusIntent = registerReceiver(mStatusReceiver,
                new IntentFilter(PowerampAPI.ACTION_STATUS_CHANGED));
        mAlbumArtIntent = registerReceiver(mAlbumArtReceiver,
                new IntentFilter(PowerampAPI.ACTION_AA_CHANGED));
        mPlayingModeIntent = registerReceiver(mPlayingModeReceiver,
                new IntentFilter(PowerampAPI.ACTION_PLAYING_MODE_CHANGED));
        processAllIntents(false);
    }

    private void unregisterReceivers() {
        if (!mReceiversRegistered) return;
        mReceiversRegistered = false;
        unregisterReceiver(mTrackReceiver);
        unregisterReceiver(mStatusReceiver);
        unregisterReceiver(mAlbumArtReceiver);
    }

    private void processTrackIntent(boolean requestedByWearable) {
        if (mConnectionState == ConnectionState.Connecting || mTrackIntent == null) return;
        if (connectIfNeeded()) {
            // Intents will be processed after GoogleApiClient successfully connects.
            return;
        }
        Bundle currentTrack = mTrackIntent.getBundleExtra(PowerampAPI.TRACK);
        if (currentTrack == null) return;
        TrackChangedEvent event = new TrackChangedEvent(
                currentTrack.getString(PowerampAPI.Track.TITLE),
                currentTrack.getString(PowerampAPI.Track.ARTIST),
                currentTrack.getString(PowerampAPI.Track.ALBUM),
                currentTrack.getInt(PowerampAPI.Track.POSITION),
                currentTrack.getInt(PowerampAPI.Track.DURATION),
                currentTrack.getInt(PowerampAPI.Track.SAMPLE_RATE),
                currentTrack.getInt(PowerampAPI.Track.BITRATE),
                currentTrack.getString(PowerampAPI.Track.CODEC)
        );
        boolean artistAlbumChanged = mPreviousEvent != null
                && (!equals(mPreviousEvent.album, event.album)
                || !equals(mPreviousEvent.artist, event.artist));
        mPreviousEvent = event;
        if (requestedByWearable || artistAlbumChanged) wakeScreen();
        send(TrackChangedEvent.PATH, event);
    }

    private void wakeScreen() {
        // PowerAmp has a bug (or it is by design?) that album art changed event is not
        // broadcasted when device is locked. Let's turn the screen for a while, when there is
        // a chance that album art has changed
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null || !mWakeLock.isHeld()) {
            //noinspection deprecation - I think I've tried everything
            mWakeLock = powerManager.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                    "PowerAmpWear"
            );
            mWakeLock.acquire(WAKE_LOCK_TIMEOUT);
        }
    }

    private void processStatusIntent() {
        if (mConnectionState == ConnectionState.Connecting || mStatusIntent == null) return;
        if (connectIfNeeded()) {
            // Intents will be processed after GoogleApiClient successfully connects.
            return;
        }
        int status = mStatusIntent.getIntExtra(PowerampAPI.STATUS, -1);
        boolean paused = mStatusIntent.getBooleanExtra(PowerampAPI.PAUSED, false);
        StatusChangedEvent event = new StatusChangedEvent(status, paused);
        send(StatusChangedEvent.PATH, event);
    }

    private void processAlbumArtIntent() {
        if (mConnectionState == ConnectionState.Connecting || mAlbumArtIntent == null) return;
        if (connectIfNeeded()) {
            // Intents will be processed after GoogleApiClient successfully connects.
            return;
        }
        Bitmap bmp = mAlbumArtIntent.getParcelableExtra(PowerampAPI.ALBUM_ART_BITMAP);
        byte[] bytes = null;
        if (bmp != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.WEBP, 80, stream);
            bytes = stream.toByteArray();
        }
        send(AlbumArtChangedEvent.PATH, new AlbumArtChangedEvent(bytes));
    }

    private void processPlayingModeIntent() {
        if (mConnectionState == ConnectionState.Connecting || mPlayingModeIntent == null) return;
        if (connectIfNeeded()) {
            // Intents will be processed after GoogleApiClient successfully connects.
            return;
        }
        int repeat = mPlayingModeIntent.getIntExtra(PowerampAPI.REPEAT, -1);
        int shuffle = mPlayingModeIntent.getIntExtra(PowerampAPI.SHUFFLE, -1);
        send(PlayingModeChangedEvent.PATH, new PlayingModeChangedEvent(shuffle, repeat));
    }

    private void send(@NonNull final String path, @Nullable final Message message) {
        if (mNodesResolver == null) return;
        mNodesResolver.resolveNodes(false, new NodesResolver.Listener() {
            @Override
            public void onNodesResolved(@NonNull NodesResolver sender, @NonNull Set<Node> nodes) {
                if (sender != mNodesResolver) {
                    // Ignore outdated requests
                    return;
                }
                byte[] bytes = message == null ? null : message.toBytes();
                for (Node node : nodes) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),
                            path, bytes);
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String path = messageEvent.getPath();
        if (path == null) return;
        switch (path) {
            case RequestsPaths.TOGGLE_PLAY_PAUSE:
                callPowerAmpAction(PowerampAPI.Commands.TOGGLE_PLAY_PAUSE);
                break;

            case RequestsPaths.NEXT_TRACK:
                callPowerAmpAction(PowerampAPI.Commands.NEXT);
                break;

            case RequestsPaths.PREV_TRACK:
                callPowerAmpAction(PowerampAPI.Commands.PREVIOUS);
                break;

            case RequestsPaths.TOGGLE_REPEAT_MODE:
                callPowerAmpAction(PowerampAPI.Commands.REPEAT);
                break;

            case RequestsPaths.TOGGLE_SHUFFLE_MODE:
                callPowerAmpAction(PowerampAPI.Commands.SHUFFLE);
                break;

            case RequestsPaths.REFRESH_TRACK_INFO:
                processAllIntents(true);
                break;

            case RequestsPaths.GET_FOLDERS:
                send(FoldersListResponse.PATH, mTracksProvider.getAvailableFolders());
                break;

            case RequestsPaths.GET_ARTISTS:
                send(ArtistsResponse.PATH, mTracksProvider.getArtists());
                break;

            case GetFilesRequest.PATH:
                processGetFilesRequest(messageEvent);
                break;

            case GetAlbumsRequest.PATH:
                processGetAlbumsRequest(messageEvent);
                break;

            case PlaySongRequest.PATH:
                processPlaySongRequest(messageEvent);
                break;
        }
    }

    private void processGetAlbumsRequest(@NonNull MessageEvent event) {
        byte[] bytes = event.getData();
        if (bytes == null) return;
        GetAlbumsRequest request = GetAlbumsRequest.fromBytes(bytes);
        AlbumsResponse response = mTracksProvider.getAlbums(request.parent);
        send(AlbumsResponse.PATH, response);
    }

    private void processGetFilesRequest(@NonNull MessageEvent event) {
        byte[] bytes = event.getData();
        if (bytes == null) return;
        GetFilesRequest request = GetFilesRequest.fromBytes(bytes);
        Parent parent = request.parent;
        if (parent == null) {
            FilesListResponse response = mTracksProvider.getAllTracks();
            send(FilesListResponse.PATH, response);
            return;
        }
        FilesListResponse response = null;
        switch (parent.type) {
            case Folder:
                response = mTracksProvider.getFilesInDirectory(parent.id);
                break;
            case Album:
                response = mTracksProvider.getFilesInAlbum(parent.id);
                break;
        }
        send(FilesListResponse.PATH, response);
    }

    private void processPlaySongRequest(@NonNull MessageEvent event) {
        byte[] bytes = event.getData();
        if (bytes == null) return;
        PlaySongRequest request = PlaySongRequest.fromBytes(bytes);
        Parent parent = request.parent;
        Uri.Builder builder = PowerampAPI.ROOT_URI.buildUpon();
        if (parent != null) {
            builder.appendEncodedPath(parent.type.value).appendEncodedPath(parent.id);
        }
        builder.appendEncodedPath("files").appendEncodedPath(request.trackId);
        callPowerAmpAction(PowerampAPI.Commands.OPEN_TO_PLAY, builder.build());
    }

    private void processAllIntents(boolean requestedByWearable) {
        processTrackIntent(requestedByWearable);
        processStatusIntent();
        processAlbumArtIntent();
        processPlayingModeIntent();
    }

    private void callPowerAmpAction(int command) {
        callPowerAmpAction(command, null);
    }

    private void callPowerAmpAction(int command, @Nullable Uri data) {
        Intent intent = PowerampAPI.newAPIIntent();
        intent.putExtra(PowerampAPI.COMMAND, command);
        if (data != null) intent.setData(data);
        startService(intent);
    }

    private static boolean equals(@Nullable Object o1, @Nullable Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }
}
