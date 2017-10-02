package com.pdyjak.powerampwear.music_browser.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.wearable.MessageEvent
import com.pdyjak.powerampwear.MessageListener
import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.common.messageExchangeHelper
import com.pdyjak.powerampwear.common.musicLibraryCache
import com.pdyjak.powerampwear.common.musicLibraryNavigator
import com.pdyjak.powerampwear.music_browser.BrowserFragmentBase
import com.pdyjak.powerampwear.music_browser.ItemViewHolder
import com.pdyjak.powerampwear.music_browser.ViewHolderFactory
import com.pdyjak.powerampwearcommon.requests.GetAlbumsRequest
import com.pdyjak.powerampwearcommon.responses.Album
import com.pdyjak.powerampwearcommon.responses.AlbumsResponse
import com.pdyjak.powerampwearcommon.responses.Parent

class AlbumsBrowserFragment : BrowserFragmentBase(), MessageListener {

    companion object {
        const val PARENT_KEY = "parent"
    }

    private class AlbumsViewHolderFactory : ViewHolderFactory {
        override fun createViewHolder(parent: ViewGroup): ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.basic_two_line_item, parent, false
            )
            return AlbumViewHolder(view)
        }
    }

    private var mParent: Parent? = null

    override fun createViewHolderFactory(): ViewHolderFactory {
        return AlbumsViewHolderFactory()
    }

    override fun tryRestoreCachedItems(): Boolean {
        val cached = activity.musicLibraryCache.getAlbums(mParent)
        if (cached !== null) {
            setItems(transform(cached.albums))
            return true
        }
        return false
    }

    override fun fetchItems() {
        val helper = activity.messageExchangeHelper
        helper.addMessageListenerWeakly(this)
        val request = GetAlbumsRequest(mParent)
        helper.sendRequest(GetAlbumsRequest.PATH, request)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args = arguments ?: throw IllegalArgumentException("Should never happen")
        mParent = args.getParcelable<Parent>(PARENT_KEY)
    }

    override fun onPause() {
        super.onPause()
        activity.messageExchangeHelper.removeMessageListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (AlbumsResponse.PATH == messageEvent.path) {
            activity.messageExchangeHelper.removeMessageListener(this)
            val bytes = messageEvent.data ?: return
            val response = AlbumsResponse.fromBytes(bytes)
            if (response !== null) {
                setItems(transform(response.albums))
                activity.musicLibraryCache.update(response)
            }
        }
    }

    private fun transform(list: List<Album>): List<AlbumItem> {
        return list.map { AlbumItem(activity.musicLibraryNavigator, it.id, it.name, it.artistName) }
    }
}
