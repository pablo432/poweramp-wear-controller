package com.pdyjak.powerampwear.music_browser

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.common.byId
import com.pdyjak.powerampwear.common.messageExchangeHelper
import com.pdyjak.powerampwear.common.musicLibraryNavigator
import com.pdyjak.powerampwear.music_browser.albums.AlbumsBrowserFragment
import com.pdyjak.powerampwear.music_browser.artists.ArtistsBrowserFragment
import com.pdyjak.powerampwear.music_browser.categories.CategorySelectionFragment
import com.pdyjak.powerampwear.music_browser.files.FilesBrowserFragment
import com.pdyjak.powerampwear.music_browser.folders.FoldersBrowserFragment
import com.pdyjak.powerampwearcommon.requests.GetAlbumsRequest
import com.pdyjak.powerampwearcommon.requests.GetFilesRequest
import com.pdyjak.powerampwearcommon.requests.PlaySongRequest
import com.pdyjak.powerampwearcommon.requests.RequestsPaths
import com.pdyjak.powerampwearcommon.responses.Parent

class LibraryExplorerFragment : Fragment() {

    companion object {
        private const val CATEGORY_FRAGMENT_TAG = "category"
        private const val BACK_BUTTON_ANIMATION_DURATION: Long = 300
    }

    private val categorySelectedEventHandler = { args: CategorySelectedEventArgs ->
        when (args.item.path) {
            RequestsPaths.GET_FOLDERS -> replaceFragment(FoldersBrowserFragment(), false)
            GetAlbumsRequest.PATH -> startAlbumsBrowser(null, false)
            RequestsPaths.GET_ARTISTS -> replaceFragment(ArtistsBrowserFragment(), false)
            GetFilesRequest.PATH -> startFilesBrowser(null, false, args.scrollTo)
            "queue" -> startFilesBrowser(Parent.forQueue(), false, args.scrollTo)
        }
    }

    private val folderSelectedEventHandler = { args: FolderSelectedEventArgs ->
        val parent = Parent(args.item.id, Parent.Type.Folder)
        startFilesBrowser(parent, args.fromPlayer, args.scrollTo)
    }

    private val albumSelectedEventHandler = { args: AlbumSelectedEventArgs ->
        val parent = Parent(args.item.id, Parent.Type.Album)
        startFilesBrowser(parent, args.fromPlayer, args.scrollTo)
    }

    private val artistSelectedEventHandler = { args: ArtistSelectedEventArgs ->
        val parent = Parent(args.item.id, Parent.Type.Artist)
        startAlbumsBrowser(parent, args.fromPlayer)
    }

    private val fileSelectedEventHandler = { args: FileSelectedEventArgs ->
        val request = PlaySongRequest(args.item.trackId, args.item.contextualId, args.item.parent)
        activity.messageExchangeHelper.sendRequest(PlaySongRequest.PATH, request)
    }

    private inner class Views(view: View) {
        private val mBackArrow: View = view byId R.id.back_arrow

        var backArrowVisible: Boolean = false
            get
            set(visible) {
                if (field == visible) return
                field = visible
                val start = if (visible) 0f else 1f
                val end = if (visible) 1f else 0f
                val animator = ValueAnimator.ofFloat(start, end)
                animator.duration = BACK_BUTTON_ANIMATION_DURATION
                animator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    mBackArrow.scaleX = value
                    mBackArrow.scaleY = value
                }
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        if (visible) mBackArrow.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        if (!visible) mBackArrow.visibility = View.GONE
                        animator.removeListener(this)
                    }
                })
                animator.start()
            }

        init {
            mBackArrow.setOnClickListener(View.OnClickListener {
                val fm = childFragmentManager
                if (fm.findFragmentByTag(CATEGORY_FRAGMENT_TAG) === null
                        && fm.backStackEntryCount == 0) {
                    val categoryFragment = CategorySelectionFragment()
                    replaceFragmentImpl(categoryFragment, true, CATEGORY_FRAGMENT_TAG)
                    backArrowVisible = false
                    return@OnClickListener
                }
                if (fm.backStackEntryCount == 1) backArrowVisible = false
                fm.popBackStackImmediate()
            })
        }
    }

    private var mViews: Views? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.library_explorer, container, false)
        mViews = Views(view)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if (view === null) throw IllegalStateException("Should never happen")
        val fragment = CategorySelectionFragment()
        childFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment, CATEGORY_FRAGMENT_TAG)
                .commit()
    }

    override fun onResume() {
        super.onResume()
        val navigator = activity.musicLibraryNavigator
        navigator.onCategorySelected += categorySelectedEventHandler
        navigator.onFolderSelected += folderSelectedEventHandler
        navigator.onAlbumSelected += albumSelectedEventHandler
        navigator.onArtistSelected += artistSelectedEventHandler
        navigator.onFileSelected += fileSelectedEventHandler
    }

    override fun onPause() {
        val navigator = activity.musicLibraryNavigator
        navigator.onCategorySelected -= categorySelectedEventHandler
        navigator.onFolderSelected -= folderSelectedEventHandler
        navigator.onAlbumSelected -= albumSelectedEventHandler
        navigator.onArtistSelected -= artistSelectedEventHandler
        navigator.onFileSelected -= fileSelectedEventHandler
        super.onPause()
    }

    override fun onDestroyView() {
        mViews = null
        super.onDestroyView()
    }

    private fun startAlbumsBrowser(parent: Parent?, fromPlayer: Boolean) {
        val args = Bundle()
        args.putParcelable(AlbumsBrowserFragment.PARENT_KEY, parent)
        val fragment = AlbumsBrowserFragment()
        fragment.arguments = args
        replaceFragment(fragment, fromPlayer)
    }

    private fun startFilesBrowser(parent: Parent?, fromPlayer: Boolean, scrollTo: String?) {
        val args = Bundle()
        args.putParcelable(FilesBrowserFragment.PARENT_KEY, parent)
        args.putString(BrowserFragmentBase.SCROLL_DESTINATION_KEY, scrollTo)
        val fragment = FilesBrowserFragment()
        fragment.arguments = args
        replaceFragment(fragment, fromPlayer)
    }

    private fun replaceFragment(fragment: Fragment, skipBackStack: Boolean) {
        replaceFragmentImpl(fragment, skipBackStack, null)
        mViews?.backArrowVisible = true
    }

    private fun replaceFragmentImpl(fragment: Fragment, skipBackStack: Boolean, tag: String?) {
        // https://stackoverflow.com/questions/17148285/replacing-a-fragment-with-another-fragment-of-the-same-class
        // Android is shit
        val fm = childFragmentManager
        val current = fm.findFragmentById(R.id.fragment_container)
        if (current !== null && current.javaClass == fragment.javaClass
                && current is BrowserFragmentBase) {
            val currentArgs = current.arguments
            if (currentArgs !== null) {
                val newArgs = fragment.arguments
                currentArgs.putAll(newArgs)
            }
            current.refresh()
            return
        }
        if (skipBackStack) {
            while (fm.backStackEntryCount > 0) fm.popBackStackImmediate()
        }
        val transaction = fm.beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left,
                        R.animator.slide_in_left, R.animator.slide_out_right)
                .replace(R.id.fragment_container, fragment, tag)
        if (!skipBackStack) transaction.addToBackStack(null)
        transaction.commit()
    }
}
