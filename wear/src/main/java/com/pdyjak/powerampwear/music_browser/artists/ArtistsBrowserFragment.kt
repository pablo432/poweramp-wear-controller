package com.pdyjak.powerampwear.music_browser.artists

import android.view.LayoutInflater
import android.view.ViewGroup

import com.google.android.gms.wearable.MessageEvent
import com.pdyjak.powerampwear.MessageListener
import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.messageExchangeHelper
import com.pdyjak.powerampwear.musicLibraryCache
import com.pdyjak.powerampwear.musicLibraryNavigator
import com.pdyjak.powerampwear.music_browser.BrowserFragmentBase
import com.pdyjak.powerampwear.music_browser.ItemViewHolder
import com.pdyjak.powerampwear.music_browser.ViewHolderFactory
import com.pdyjak.powerampwearcommon.requests.RequestsPaths
import com.pdyjak.powerampwearcommon.responses.Artist
import com.pdyjak.powerampwearcommon.responses.ArtistsResponse

class ArtistsBrowserFragment : BrowserFragmentBase(), MessageListener {

    private class ArtistsViewHolderFactory : ViewHolderFactory {
        override fun createViewHolder(parent: ViewGroup): ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.basic_two_line_item, parent, false
            )
            return ArtistViewHolder(view)
        }
    }

    override fun createViewHolderFactory(): ViewHolderFactory {
        return ArtistsViewHolderFactory()
    }

    override fun tryRestoreCachedItems(): Boolean {
        val cached = activity.musicLibraryCache.artists
        if (cached !== null) {
            setItems(transform(cached.getArtists()))
            return true
        }
        return false
    }

    override fun fetchItems() {
        val helper = activity.messageExchangeHelper
        helper.addMessageListenerWeakly(this)
        helper.sendRequest(RequestsPaths.GET_ARTISTS)
    }

    override fun onPause() {
        super.onPause()
        activity.messageExchangeHelper.removeMessageListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (ArtistsResponse.PATH == messageEvent.path) {
            activity.messageExchangeHelper.removeMessageListener(this)
            val bytes = messageEvent.data ?: return
            val response = ArtistsResponse.fromBytes(bytes)
            if (response !== null) {
                setItems(transform(response.artists))
                activity.musicLibraryCache.update(response)
            }
        }
    }

    private fun transform(artists: List<Artist>): List<ArtistItem> {
        val navigator = activity.musicLibraryNavigator
        return artists.map { ArtistItem(navigator, it.id, it.name, it.songsCount) }
    }
}
