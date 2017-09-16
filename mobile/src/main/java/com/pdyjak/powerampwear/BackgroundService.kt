package com.pdyjak.powerampwear

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.RemoteException
import android.text.TextUtils

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.google.firebase.FirebaseApp
import com.maxmpz.poweramp.player.PowerampAPI
import com.pdyjak.powerampwearcommon.ConnectionState
import com.pdyjak.powerampwearcommon.Message
import com.pdyjak.powerampwearcommon.NodesResolver
import com.pdyjak.powerampwearcommon.events.AlbumArtChangedEvent
import com.pdyjak.powerampwearcommon.events.PlayingModeChangedEvent
import com.pdyjak.powerampwearcommon.events.StatusChangedEvent
import com.pdyjak.powerampwearcommon.events.TrackChangedEvent
import com.pdyjak.powerampwearcommon.events.TrackPositionSyncEvent
import com.pdyjak.powerampwearcommon.requests.FindParentRequest
import com.pdyjak.powerampwearcommon.requests.GetAlbumsRequest
import com.pdyjak.powerampwearcommon.requests.GetFilesRequest
import com.pdyjak.powerampwearcommon.requests.PlaySongRequest
import com.pdyjak.powerampwearcommon.requests.RequestsPaths
import com.pdyjak.powerampwearcommon.responses.AlbumsResponse
import com.pdyjak.powerampwearcommon.responses.ArtistsResponse
import com.pdyjak.powerampwearcommon.responses.FilesListResponse
import com.pdyjak.powerampwearcommon.responses.FindParentResponse
import com.pdyjak.powerampwearcommon.responses.FoldersListResponse
import com.pdyjak.powerampwearcommon.responses.Parent

import java.io.ByteArrayOutputStream
import java.io.File

/**
 * 1. Receives PowerAmp events and sends them to the Wearable device
 * 2. Handles requests from wearable device, like toggle play/pause, switching between songs and
 * providing lists of songs.
 */
class BackgroundService : Service(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    companion object {
        private const val PROCESS_POWERAMP_EVENTS_CAPABILITY_NAME = "process_poweramp_events"
        private const val WAKE_LOCK_TIMEOUT: Long = 1500
    }

    private val mBinder = object : IBackgroundService.Stub() {
        @Throws(RemoteException::class)
        override fun setShowAlbumArt(show: Boolean) {
            mHandler.post { setShouldShowAlbumArt(show) }
        }

        @Throws(RemoteException::class)
        override fun setWakeWhenChangingSongs(wake: Boolean) {
            mHandler.post { setShouldWakeWhenChangingSongs(wake) }
        }
    }

    private val mHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private lateinit var mTracksProvider: TracksProvider
    private lateinit var mGoogleApiClient: GoogleApiClient

    private var mTrackIntent: Intent? = null
    private var mStatusIntent: Intent? = null
    private var mAlbumArtIntent: Intent? = null
    private var mPlayingModeIntent: Intent? = null
    private var mTrackPosIntent: Intent? = null
    private var mConnectionState: ConnectionState? = null
    private var mNodesResolver: NodesResolver? = null
    private var mWakeLock: PowerManager.WakeLock? = null
    private var mPreviousEvent: TrackChangedEvent? = null
    private var mReceiversRegistered: Boolean = false
    private var mShouldShowAlbumArt: Boolean = false
    private var mShouldWakeWhenChangingSongs: Boolean = false

    private val mTrackReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mTrackIntent = intent
            processTrackIntent(false, intent)
        }
    }

    private val mStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mStatusIntent = intent
            processStatusIntent(intent)
        }
    }

    private val mAlbumArtReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mAlbumArtIntent = intent
            processAlbumArtIntent(intent)
        }
    }

    private val mPlayingModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mPlayingModeIntent = intent
            processPlayingModeIntent(intent)
        }
    }

    private val mTrackPosSyncReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mTrackPosIntent = intent
            processTrackPositionIntent(intent)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.ENABLE_CRASH_REPORTING) FirebaseApp.initializeApp(this)
        mShouldShowAlbumArt = settingsManager.shouldShowAlbumArt
        mShouldWakeWhenChangingSongs = settingsManager.shouldWakeWhenChangingSongs
        mTracksProvider = TracksProvider(contentResolver)
        mGoogleApiClient = GoogleApiClient.Builder(this, this, this)
                .addApi(Wearable.API)
                .build()
        mConnectionState = ConnectionState.Connecting
        mGoogleApiClient.connect()
    }

    override fun onConnected(connectionHind: Bundle?) {
        mConnectionState = ConnectionState.Connected
        mNodesResolver = NodesResolver(mGoogleApiClient,
                PROCESS_POWERAMP_EVENTS_CAPABILITY_NAME)
        Wearable.MessageApi.addListener(mGoogleApiClient, this)
        registerReceivers()
    }

    override fun onConnectionSuspended(cause: Int) {
        mConnectionState = ConnectionState.Suspended
        unregisterReceivers()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        mConnectionState = ConnectionState.FailedToConnect
    }

    private fun setShouldShowAlbumArt(shouldShow: Boolean) {
        if (mShouldShowAlbumArt == shouldShow) return
        mShouldShowAlbumArt = shouldShow
        processAlbumArtIntent(mAlbumArtIntent)
    }

    private fun setShouldWakeWhenChangingSongs(shouldWake: Boolean) {
        if (mShouldWakeWhenChangingSongs == shouldWake) return
        mShouldWakeWhenChangingSongs = shouldWake
        processAlbumArtIntent(mAlbumArtIntent)
    }

    /*
     * Returns true, if an attempt to connect has been made (meaning that any functions which want
     * to send data to wearable device, should
     */
    private fun connectIfNeeded(): Boolean {
        if (mConnectionState === null || mConnectionState == ConnectionState.FailedToConnect) {
            mConnectionState = ConnectionState.Connecting
            mGoogleApiClient.connect()
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceivers()
        mConnectionState = null
        Wearable.MessageApi.removeListener(mGoogleApiClient, this)
        if (mNodesResolver !== null) mNodesResolver!!.dispose()
        mGoogleApiClient.disconnect()
    }

    private fun registerReceivers() {
        mReceiversRegistered = true
        mTrackIntent = registerReceiver(mTrackReceiver,
                IntentFilter(PowerampAPI.ACTION_TRACK_CHANGED))
        mStatusIntent = registerReceiver(mStatusReceiver,
                IntentFilter(PowerampAPI.ACTION_STATUS_CHANGED))
        mAlbumArtIntent = registerReceiver(mAlbumArtReceiver,
                IntentFilter(PowerampAPI.ACTION_AA_CHANGED))
        mPlayingModeIntent = registerReceiver(mPlayingModeReceiver,
                IntentFilter(PowerampAPI.ACTION_PLAYING_MODE_CHANGED))
        mTrackPosIntent = registerReceiver(mTrackPosSyncReceiver,
                IntentFilter(PowerampAPI.ACTION_TRACK_POS_SYNC))
        processAllIntents(false)
    }

    private fun unregisterReceivers() {
        if (!mReceiversRegistered) return
        mReceiversRegistered = false
        unregisterReceiver(mTrackReceiver)
        unregisterReceiver(mStatusReceiver)
        unregisterReceiver(mAlbumArtReceiver)
        unregisterReceiver(mPlayingModeReceiver)
        unregisterReceiver(mTrackPosSyncReceiver)
    }

    private fun processTrackIntent(requestedByWearable: Boolean, intent: Intent?) {
        if (mConnectionState == ConnectionState.Connecting || intent === null) return
        if (connectIfNeeded()) {
            // Intents will be processed after GoogleApiClient successfully connects.
            return
        }
        val event = createTrackChangedEvent(intent) ?: return
        val previousEvent = this.mPreviousEvent
        val artistAlbumChanged = !event.artistAlbumEquals(previousEvent)
        this.mPreviousEvent = event
        if (mShouldShowAlbumArt && mShouldWakeWhenChangingSongs
                && (requestedByWearable || artistAlbumChanged)) {
            wakeScreen()
        }
        send(TrackChangedEvent.PATH, event)
    }

    private fun createTrackChangedEvent(intent: Intent): TrackChangedEvent? {
        val currentTrack = intent.getBundleExtra(PowerampAPI.TRACK) ?: return null
        return TrackChangedEvent(
                currentTrack.getString(PowerampAPI.Track.TITLE),
                currentTrack.getString(PowerampAPI.Track.ARTIST),
                currentTrack.getString(PowerampAPI.Track.ALBUM),
                currentTrack.getInt(PowerampAPI.Track.POSITION),
                currentTrack.getInt(PowerampAPI.Track.DURATION),
                currentTrack.getInt(PowerampAPI.Track.SAMPLE_RATE),
                currentTrack.getInt(PowerampAPI.Track.BITRATE),
                currentTrack.getString(PowerampAPI.Track.CODEC)
        )
    }

    @Suppress("DEPRECATION")
    private fun wakeScreen() {
        // PowerAmp has a bug (or it is by design?) that album art changed event is not
        // broadcasted when device is locked. Let's turn the screen for a while, when there is
        // a chance that album art has changed
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        var wakeLock = mWakeLock
        if (wakeLock === null || !wakeLock.isHeld) {
            wakeLock = powerManager.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                    "PowerAmpWear"
            )
            wakeLock.acquire(WAKE_LOCK_TIMEOUT)
            mWakeLock = wakeLock
        }
    }

    private fun processStatusIntent(intent: Intent?) {
        if (mConnectionState == ConnectionState.Connecting || intent === null) return
        if (connectIfNeeded()) {
            // Intents will be processed after GoogleApiClient successfully connects.
            return
        }
        val status = intent.getIntExtra(PowerampAPI.STATUS, -1)
        val paused = intent.getBooleanExtra(PowerampAPI.PAUSED, false)
        val event = StatusChangedEvent(status, paused)
        send(StatusChangedEvent.PATH, event)
    }

    private fun processAlbumArtIntent(intent: Intent?) {
        if (mConnectionState == ConnectionState.Connecting || intent === null) return
        if (connectIfNeeded()) {
            // Intents will be processed after GoogleApiClient successfully connects.
            return
        }
        var bytes: ByteArray? = null
        if (mShouldShowAlbumArt) {
            val bmp = intent.getParcelableExtra<Bitmap>(PowerampAPI.ALBUM_ART_BITMAP)
            if (bmp !== null) {
                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.WEBP, 80, stream)
                bytes = stream.toByteArray()
            }
        }
        send(AlbumArtChangedEvent.PATH, AlbumArtChangedEvent(bytes))
    }

    private fun processPlayingModeIntent(intent: Intent?) {
        if (mConnectionState == ConnectionState.Connecting || intent === null) return
        if (connectIfNeeded()) {
            // Intents will be processed after GoogleApiClient successfully connects.
            return
        }
        val repeat = intent.getIntExtra(PowerampAPI.REPEAT, -1)
        val shuffle = intent.getIntExtra(PowerampAPI.SHUFFLE, -1)
        send(PlayingModeChangedEvent.PATH, PlayingModeChangedEvent(shuffle, repeat))
    }

    private fun processTrackPositionIntent(intent: Intent?) {
        if (mConnectionState == ConnectionState.Connecting || intent === null) return
        if (connectIfNeeded()) {
            // Intents will be processed after GoogleApiClient successfully connects.
            return
        }
        val pos = intent.getIntExtra(PowerampAPI.Track.POSITION, 0)
        send(TrackPositionSyncEvent.PATH, TrackPositionSyncEvent(pos))
    }

    private fun send(path: String, message: Message?) {
        val nodesResolver = this.mNodesResolver ?: return
        nodesResolver.resolveNodes(false, object : NodesResolver.Listener {
            override fun onNodesResolved(sender: NodesResolver, nodes: Set<Node>) {
                if (sender !== nodesResolver) {
                    // Ignore outdated requests
                    return
                }
                val bytes = message?.toBytes()
                for (node in nodes) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node.id, path, bytes)
                }
            }

            override fun onFailed() {
            }
        })
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val path = messageEvent.path ?: return
        when (path) {
            RequestsPaths.TOGGLE_PLAY_PAUSE ->
                callPowerAmpAction(PowerampAPI.Commands.TOGGLE_PLAY_PAUSE)

            RequestsPaths.NEXT_TRACK -> callPowerAmpAction(PowerampAPI.Commands.NEXT)

            RequestsPaths.PREV_TRACK -> callPowerAmpAction(PowerampAPI.Commands.PREVIOUS)

            RequestsPaths.VOLUME_DOWN -> volumeDown()

            RequestsPaths.VOLUME_UP -> volumeUp()

            RequestsPaths.SYNC_TRACK_POSITION -> callPowerAmpAction(PowerampAPI.Commands.POS_SYNC)

            RequestsPaths.TOGGLE_REPEAT_MODE -> callPowerAmpAction(PowerampAPI.Commands.REPEAT)

            RequestsPaths.TOGGLE_SHUFFLE_MODE -> callPowerAmpAction(PowerampAPI.Commands.SHUFFLE)

            RequestsPaths.REFRESH_TRACK_INFO -> {
                processAllIntents(true)
                callPowerAmpAction(PowerampAPI.Commands.POS_SYNC)
            }

            RequestsPaths.GET_FOLDERS -> {
                val files = mTracksProvider.availableFolders
                if (files !== null) send(FoldersListResponse.PATH, files)
            }

            RequestsPaths.GET_ARTISTS -> send(ArtistsResponse.PATH, mTracksProvider.artists)

            GetFilesRequest.PATH -> processGetFilesRequest(messageEvent)

            GetAlbumsRequest.PATH -> processGetAlbumsRequest(messageEvent)

            PlaySongRequest.PATH -> processPlaySongRequest(messageEvent)

            FindParentRequest.PATH -> findParent(messageEvent)
        }
    }

    private fun volumeDown() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0)
    }

    private fun volumeUp() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0)
    }

    private fun processGetAlbumsRequest(event: MessageEvent) {
        val bytes = event.data ?: return
        val request = GetAlbumsRequest.fromBytes(bytes)
        val response = mTracksProvider.getAlbums(request.parent)
        send(AlbumsResponse.PATH, response)
    }

    private fun processGetFilesRequest(event: MessageEvent) {
        val bytes = event.data ?: return
        val request = GetFilesRequest.fromBytes(bytes)
        val parent = request.parent
        if (parent === null) {
            val response = mTracksProvider.allTracks
            if (response !== null) send(FilesListResponse.PATH, response)
            return
        }
        if ("queue" == parent.id) { // have mercy
            val queue = mTracksProvider.queue
            if (queue !== null) send(FilesListResponse.PATH, queue)
            return
        }
        val response: FilesListResponse?
        when (parent.type) {
            Parent.Type.Folder -> response = mTracksProvider.getFilesInDirectory(parent.id)
            Parent.Type.Album -> response = mTracksProvider.getFilesInAlbum(parent.id)
            else -> response = null
        }
        send(FilesListResponse.PATH, response)
    }

    private fun processPlaySongRequest(event: MessageEvent) {
        val bytes = event.data ?: return
        val request = PlaySongRequest.fromBytes(bytes)
        val parent = request.parent
        val builder = PowerampAPI.ROOT_URI.buildUpon()
        if (parent !== null) {
            builder.appendEncodedPath(parent.type.value)
            if ("queue" != parent.id) builder.appendEncodedPath(parent.id)
        }
        if (parent === null || parent.type != Parent.Type.Queue) {
            builder.appendEncodedPath("files")
        }
        builder.appendEncodedPath(request.contextualId ?: request.trackId)
        callPowerAmpAction(PowerampAPI.Commands.OPEN_TO_PLAY, builder.build())
    }

    private fun findParent(messageEvent: MessageEvent) {
        if (messageEvent.data == null) return

        // Maybe there will be a better time to make use of it, ignore now
        // FindParentRequest request = FindParentRequest.fromBytes(bytes);
        val trackIntent = this.mTrackIntent ?: return
        val bundle = trackIntent.getBundleExtra(PowerampAPI.TRACK) ?: return

        val category = trackIntent.getBundleExtra(PowerampAPI.TRACK).getInt(PowerampAPI.Track.CAT)
        var response: FindParentResponse? = null
        var folderAttempted = false
        when (category) {
            PowerampAPI.Cats.ROOT -> response = mTracksProvider.getAllTracks(
                    bundle.getString(PowerampAPI.Track.TITLE))

            PowerampAPI.Cats.QUEUE -> response = mTracksProvider.getQueueParent(
                    bundle.getString(PowerampAPI.Track.TITLE))

            PowerampAPI.Cats.ALBUMS, PowerampAPI.Cats.ARTISTS_ID_ALBUMS ->
                response = findFilesInCurrentAlbum(bundle)

            PowerampAPI.Cats.FOLDERS -> {
                response = findFilesInCurrentDirectory(bundle)
                folderAttempted = true
            }
        }
        if (!folderAttempted && response === null) {
            // Fallback to folders
            response = findFilesInCurrentDirectory(bundle)
        }
        send(FindParentResponse.PATH, response ?: return)
    }

    private fun findFilesInCurrentAlbum(track: Bundle): FindParentResponse? {
        val albumName = track.getString(PowerampAPI.Track.ALBUM) ?: return null
        return mTracksProvider.getFilesInCurrentAlbum(albumName,
                track.getString(PowerampAPI.Track.TITLE))
    }

    private fun findFilesInCurrentDirectory(track: Bundle): FindParentResponse? {
        val path = track.getString(PowerampAPI.Track.PATH)
        if (TextUtils.isEmpty(path)) return null
        val current = File(path)
        return mTracksProvider.getFilesInDirectory(current.parentFile,
                track.getString(PowerampAPI.Track.TITLE))
    }

    private fun processAllIntents(requestedByWearable: Boolean) {
        processTrackIntent(requestedByWearable, mTrackIntent)
        processStatusIntent(mStatusIntent)
        processAlbumArtIntent(mAlbumArtIntent)
        processPlayingModeIntent(mPlayingModeIntent)
        // processTrackPositionIntent(); // Don't.
    }

    private fun callPowerAmpAction(command: Int, data: Uri? = null) {
        val intent = PowerampAPI.newAPIIntent()
        intent.putExtra(PowerampAPI.COMMAND, command)
        if (data !== null) intent.data = data
        startService(intent)
    }

}
