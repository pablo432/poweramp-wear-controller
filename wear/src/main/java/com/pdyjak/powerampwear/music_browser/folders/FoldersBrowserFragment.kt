package com.pdyjak.powerampwear.music_browser.folders

import android.view.LayoutInflater
import android.view.ViewGroup

import com.google.android.gms.wearable.MessageEvent
import com.pdyjak.powerampwear.MessageListener
import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.messageExchangeHelper
import com.pdyjak.powerampwear.musicLibraryCache
import com.pdyjak.powerampwear.musicLibraryNavigator
import com.pdyjak.powerampwear.music_browser.BrowserFragmentBase
import com.pdyjak.powerampwear.music_browser.Clickable
import com.pdyjak.powerampwear.music_browser.ItemViewHolder
import com.pdyjak.powerampwear.music_browser.ViewHolderFactory
import com.pdyjak.powerampwearcommon.requests.RequestsPaths
import com.pdyjak.powerampwearcommon.responses.Folder
import com.pdyjak.powerampwearcommon.responses.FoldersListResponse

class FoldersBrowserFragment : BrowserFragmentBase(), MessageListener {

    private class FoldersViewHolderFactory : ViewHolderFactory {
        override fun createViewHolder(parent: ViewGroup): ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.basic_two_line_item, parent, false
            )
            return FolderViewHolder(view)
        }
    }

    override fun createViewHolderFactory(): ViewHolderFactory {
        return FoldersViewHolderFactory()
    }

    override fun tryRestoreCachedItems(): Boolean {
        val cached = activity.musicLibraryCache.foldersList
        setItems(transform(cached?.foldersList ?: return false))
        return true
    }

    override fun fetchItems() {
        val helper = activity.messageExchangeHelper
        helper.addMessageListenerWeakly(this)
        helper.sendRequest(RequestsPaths.GET_FOLDERS)
    }

    override fun onPause() {
        super.onPause()
        activity.messageExchangeHelper.removeMessageListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (FoldersListResponse.PATH == messageEvent.path) {
            activity.messageExchangeHelper.removeMessageListener(this)
            val bytes = messageEvent.data ?: return
            val response = FoldersListResponse.fromBytes(bytes)
            if (response != null) {
                activity.musicLibraryCache.update(response)
                setItems(transform(response.foldersList))
            }
        }
    }

    private fun transform(folders: List<Folder>): List<Clickable> {
        return folders.map {
            FolderItem(activity.musicLibraryNavigator, it.id, it.name, it.parentName)
        }
    }
}
