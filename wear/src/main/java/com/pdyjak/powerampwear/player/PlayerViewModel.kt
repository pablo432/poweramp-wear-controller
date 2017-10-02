package com.pdyjak.powerampwear.player

import android.os.Handler
import android.os.Looper
import com.google.android.gms.wearable.MessageEvent
import com.maxmpz.poweramp.player.PowerampAPI
import com.pdyjak.powerampwear.MessageExchangeHelper
import com.pdyjak.powerampwear.MessageListener
import com.pdyjak.powerampwear.common.Event
import com.pdyjak.powerampwear.common.SimpleEvent
import com.pdyjak.powerampwear.music_browser.MusicLibraryNavigator
import com.pdyjak.powerampwear.music_browser.albums.AlbumItem
import com.pdyjak.powerampwear.music_browser.categories.CategoryItem
import com.pdyjak.powerampwear.music_browser.folders.FolderItem
import com.pdyjak.powerampwear.settings.SettingsManager
import com.pdyjak.powerampwearcommon.events.PlayingModeChangedEvent
import com.pdyjak.powerampwearcommon.events.StatusChangedEvent
import com.pdyjak.powerampwearcommon.events.TrackChangedEvent
import com.pdyjak.powerampwearcommon.events.TrackPositionSyncEvent
import com.pdyjak.powerampwearcommon.requests.FindParentRequest
import com.pdyjak.powerampwearcommon.requests.GetFilesRequest
import com.pdyjak.powerampwearcommon.requests.RequestsPaths
import com.pdyjak.powerampwearcommon.responses.FindParentResponse
import com.pdyjak.powerampwearcommon.responses.Parent

data class PositionChangedEventArgs(val position: Int, val duration: Int)

class PlayerViewModel(private val mSettingsManager: SettingsManager,
                      private val mMessageExchangeHelper: MessageExchangeHelper,
                      private val mAmbientModeStateProvider: AmbientModeStateProvider,
                      private val mMusicLibraryNavigator: MusicLibraryNavigator)
    : MessageListener {

    private companion object {
        const val DEFAULT_TIMEOUT = 5000
        const val TRACK_POSITION_UPDATE_DELAY = 1000
        const val SECONDS_PER_TICK = TRACK_POSITION_UPDATE_DELAY / 1000

        const val REPEAT_MAX_LEVEL = 3
        const val SHUFFLE_MAX_LEVEL = 4
    }

    enum class State {
        Loading,
        Failure,
        Ok
    }

    enum class ShuffleMode(val value: Int) {
        Off(0),
        All(1),
        Songs(2),
        Lists(3),
        SongsLists(4);

        companion object {
            fun from(ordinal: Int): ShuffleMode? = values().let {
                return if (it.size < ordinal) it[ordinal] else null
            }
        }
    }

    enum class RepeatMode(val value: Int) {
        Off(0),
        List(1),
        AdvanceList(2),
        Song(3);

        companion object {
            fun from(ordinal: Int): RepeatMode? = values().let {
                return if (it.size < ordinal) it[ordinal] else null
            }
        }
    }

    val onClockVisibilityChanged = SimpleEvent()
    val onProgressbarVisibilityChanged = SimpleEvent()
    val onStateChanged = SimpleEvent()
    val onPauseChanged = SimpleEvent()
    val onTrackInfoChanged = SimpleEvent()
    val onRepeatModeChanged = SimpleEvent()
    val onShuffleModeChanged = SimpleEvent()
    val onPositionChanged = Event<PositionChangedEventArgs>()

    private var mCurrentTrackPosition = 0
    private var mCurrentTrackDuration = 0

    private val mClockSettingsHandler = { shouldShowClock = mSettingsManager.shouldShowClock() }
    private val mAmbientModeHandler = {
        if (mAmbientModeStateProvider.isInAmbientMode) {
            mHandler.removeCallbacks(mTrackTimeUpdateRunnable)
        } else {
            mMessageExchangeHelper.sendRequest(RequestsPaths.SYNC_TRACK_POSITION)
        }
        shouldShowProgressbar = !mAmbientModeStateProvider.isInAmbientMode
    }

    private val mTimeoutRunnable = Runnable { state = State.Failure }

    private val mTrackTimeUpdateRunnable = object: Runnable {
        override fun run() {
            mCurrentTrackPosition += SECONDS_PER_TICK
            notifyTrackPositionChanged()
            mHandler.removeCallbacks(this)
            mHandler.postDelayed(this, TRACK_POSITION_UPDATE_DELAY.toLong())
        }
    }

    private val mHandler = Handler(Looper.getMainLooper())
    private var mActive = false
    private var mTimeoutRunnablePosted = false

    var state: State = State.Loading
        get
        private set(value) {
            if (field == value) return
            field = value
            onStateChanged()
        }

    var paused: Boolean = true
        get
        private set(value) {
            if (field == value) return
            field = value
            onPauseChanged()
        }

    var repeatMode: RepeatMode = RepeatMode.Off
        get
        private set(value) {
            if (field == value) return
            field = value
            onRepeatModeChanged()
        }

    var shuffleMode: ShuffleMode = ShuffleMode.Off
        get
        private set(value) {
            if (field == value) return
            field = value
            onShuffleModeChanged()
        }

    var shouldShowClock: Boolean = mSettingsManager.shouldShowClock()
        get
        private set(value) {
            if (field == value) return
            field = value
            onClockVisibilityChanged()
        }

    var shouldShowProgressbar: Boolean = !mAmbientModeStateProvider.isInAmbientMode
        get
        private set(value) {
            if (field == value) return
            field = value
            onProgressbarVisibilityChanged()
        }

    var title: String = ""
        get
        private set

    var artistAlbum: String = ""
        get
        private set

    fun onResume() {
        if (mActive) return
        mActive = true
        if (!mTimeoutRunnablePosted) {
            mTimeoutRunnablePosted = true
            mHandler.postDelayed(mTimeoutRunnable, DEFAULT_TIMEOUT.toLong())
        }
        mMessageExchangeHelper.addMessageListenerWeakly(this)
        mMessageExchangeHelper.sendRequest(RequestsPaths.REFRESH_TRACK_INFO)
        mSettingsManager.onClockSettingChanged += mClockSettingsHandler
        mAmbientModeStateProvider.onAmbientModeChanged += mAmbientModeHandler
        shouldShowClock = mSettingsManager.shouldShowClock()
        shouldShowProgressbar = !mAmbientModeStateProvider.isInAmbientMode
    }

    fun onPause() {
        if (!mActive) return
        mActive = false
        mMessageExchangeHelper.removeMessageListener(this)
        mSettingsManager.onClockSettingChanged -= mClockSettingsHandler
        mAmbientModeStateProvider.onAmbientModeChanged -= mAmbientModeHandler
        removeTimeoutCallbackFromHandler()
        mHandler.removeCallbacks(mTrackTimeUpdateRunnable)
    }

    private fun removeTimeoutCallbackFromHandler() {
        if (!mTimeoutRunnablePosted) return
        mTimeoutRunnablePosted = false
        mHandler.removeCallbacks(mTimeoutRunnable)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            TrackChangedEvent.PATH -> {
                removeTimeoutCallbackFromHandler()
                state = State.Ok
                processTrackInfo(TrackChangedEvent.fromBytes(messageEvent.data))
            }

            StatusChangedEvent.PATH -> {
                processStatusInfo(StatusChangedEvent.fromBytes(messageEvent.data))
            }

            PlayingModeChangedEvent.PATH -> {
                processPlayingModeInfo(PlayingModeChangedEvent.fromBytes(messageEvent.data))
            }

            TrackPositionSyncEvent.PATH -> {
                processTrackPositionSyncInfo(TrackPositionSyncEvent.fromBytes(messageEvent.data))
            }

            FindParentResponse.PATH -> {
                goToLibrary(FindParentResponse.fromBytes(messageEvent.data))
            }
        }
    }

    private fun processTrackInfo(event: TrackChangedEvent) {
        val changed = title != event.title
        title = event.title
        mCurrentTrackDuration = event.duration
        var artistAlbum = ""
        if (event.artist != null) artistAlbum = event.artist
        if (event.album != null) {
            if (artistAlbum.isNotEmpty()) artistAlbum += " - "
            artistAlbum += event.album
        }
        this.artistAlbum = artistAlbum
        if (changed || this.artistAlbum != artistAlbum) onTrackInfoChanged()
    }

    private fun processStatusInfo(event: StatusChangedEvent) {
        paused = event.status != PowerampAPI.Status.TRACK_PLAYING || event.paused
    }

    private fun processPlayingModeInfo(event: PlayingModeChangedEvent) {
        val shuffle = ShuffleMode.from(event.shuffleMode)
        if (shuffle != null) shuffleMode = shuffle
        val repeat = RepeatMode.from(event.repeatMode)
        if (repeat != null) repeatMode = repeat
    }

    private fun processTrackPositionSyncInfo(event: TrackPositionSyncEvent) {
        mCurrentTrackPosition = event.position
        notifyTrackPositionChanged()
        if (paused) return
        mHandler.removeCallbacks(mTrackTimeUpdateRunnable)
        mHandler.postDelayed(mTrackTimeUpdateRunnable, TRACK_POSITION_UPDATE_DELAY.toLong())
    }

    private fun notifyTrackPositionChanged() {
        onPositionChanged(PositionChangedEventArgs(mCurrentTrackPosition, mCurrentTrackDuration))
    }

    private fun goToLibrary(response: FindParentResponse) {
        val parent = response.parent
        if (parent == null) {
            // All tracks
            val categoryItem = CategoryItem(mMusicLibraryNavigator, GetFilesRequest.PATH, 0, 0)
            mMusicLibraryNavigator.selectCategory(categoryItem, true, response.title)
            return
        }

        if (parent.type == Parent.Type.Queue || parent.type == Parent.Type.Folder) {
            val folderItem = FolderItem(mMusicLibraryNavigator, parent.id, null, null)
            mMusicLibraryNavigator.selectFolder(folderItem, true, response.title)
        } else if (parent.type == Parent.Type.Album) {
            val albumItem = AlbumItem(mMusicLibraryNavigator, parent.id, null, null)
            mMusicLibraryNavigator.selectAlbum(albumItem, true, response.title)
        }
    }

    fun goToLibrary() {
        mMessageExchangeHelper.sendRequest(FindParentRequest.PATH,
                FindParentRequest(-1))
    }

    fun togglePlayPause() {
        paused = !paused
        if (paused) {
            mHandler.removeCallbacks(mTrackTimeUpdateRunnable)
        } else {
            mMessageExchangeHelper.sendRequest(RequestsPaths.SYNC_TRACK_POSITION)
        }
        mMessageExchangeHelper.sendRequest(RequestsPaths.TOGGLE_PLAY_PAUSE)
    }

    fun toggleRepeatMode() {
        val next = (if (repeatMode.value == REPEAT_MAX_LEVEL)
            RepeatMode.from(0)
        else
            RepeatMode.from(repeatMode.value + 1)) ?: return
        repeatMode = next
        mMessageExchangeHelper.sendRequest(RequestsPaths.TOGGLE_REPEAT_MODE)
    }

    fun toggleShuffleMode() {
        val next = (if (shuffleMode.value == SHUFFLE_MAX_LEVEL)
            ShuffleMode.from(0)
        else
            ShuffleMode.from(shuffleMode.value + 1)) ?: return
        shuffleMode = next
        mMessageExchangeHelper.sendRequest(RequestsPaths.TOGGLE_SHUFFLE_MODE)
    }

    fun previousTrack() {
        mMessageExchangeHelper.sendRequest(RequestsPaths.PREV_TRACK)
    }

    fun nextTrack() {
        mMessageExchangeHelper.sendRequest(RequestsPaths.NEXT_TRACK)
    }

    fun volumeDown() {
        mMessageExchangeHelper.sendRequest(RequestsPaths.VOLUME_DOWN)
    }

    fun volumeUp() {
        mMessageExchangeHelper.sendRequest(RequestsPaths.VOLUME_UP)
    }
}
