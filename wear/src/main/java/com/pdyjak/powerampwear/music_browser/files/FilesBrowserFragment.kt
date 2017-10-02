package com.pdyjak.powerampwear.music_browser.files

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
import com.pdyjak.powerampwear.music_browser.Clickable
import com.pdyjak.powerampwear.music_browser.ItemViewHolder
import com.pdyjak.powerampwear.music_browser.ViewHolderFactory
import com.pdyjak.powerampwearcommon.requests.GetFilesRequest
import com.pdyjak.powerampwearcommon.responses.FilesListResponse
import com.pdyjak.powerampwearcommon.responses.Parent

class FilesBrowserFragment : BrowserFragmentBase(), MessageListener {

    companion object {
        const val PARENT_KEY = "parent"
    }

    private class FilesViewHolderFactory : ViewHolderFactory {
        override fun createViewHolder(parent: ViewGroup): ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.basic_two_line_item, parent, false
            )
            return FileViewHolder(view)
        }
    }

    private var mParent: Parent? = null

    override fun createViewHolderFactory(): ViewHolderFactory {
        return FilesViewHolderFactory()
    }

    override fun tryRestoreCachedItems(): Boolean {
        val cached = activity.musicLibraryCache.getFilesList(mParent) ?: return false
        setItems(transform(cached))
        return true
    }

    override fun shouldScrollTo(item: Clickable, scrollDest: String): Boolean {
        if (item !is FileItem) return false
        val fileItem = item
        return fileItem.title !== null && fileItem.title.startsWith(scrollDest)
    }

    override fun fetchItems() {
        val request = GetFilesRequest(mParent)
        val helper = activity.messageExchangeHelper
        helper.addMessageListenerWeakly(this)
        helper.sendRequest(GetFilesRequest.PATH, request)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ensureParent()
    }

    override fun onGoingToRefresh() {
        ensureParent()
    }

    private fun ensureParent() {
        val args = arguments ?: throw IllegalArgumentException("Should never happen")
        mParent = args.getParcelable<Parent>(PARENT_KEY)
    }

    override fun onPause() {
        super.onPause()
        activity.messageExchangeHelper.removeMessageListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (FilesListResponse.PATH == messageEvent.path) {
            activity.messageExchangeHelper.removeMessageListener(this)
            val bytes = messageEvent.data ?: return
            val response = FilesListResponse.fromBytes(bytes)
            if (response !== null) {
                setItems(transform(response))
                activity.musicLibraryCache.update(response)
            }
        }
    }

    private fun transform(response: FilesListResponse): List<FileItem> {
        return response.filesList.map {
            FileItem(activity.musicLibraryNavigator, response.parent, it.id, it.title, it.artist,
                    it.album, it.duration, it.contextualId)
        }
    }
}
