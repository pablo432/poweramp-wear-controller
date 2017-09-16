package com.pdyjak.powerampwear.music_browser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdyjak.powerampwear.App;
import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwear.music_browser.albums.AlbumItem;
import com.pdyjak.powerampwear.music_browser.albums.AlbumsBrowserFragment;
import com.pdyjak.powerampwear.music_browser.artists.ArtistItem;
import com.pdyjak.powerampwear.music_browser.artists.ArtistsBrowserFragment;
import com.pdyjak.powerampwear.music_browser.categories.CategoryItem;
import com.pdyjak.powerampwear.music_browser.categories.CategorySelectionFragment;
import com.pdyjak.powerampwear.music_browser.files.FileItem;
import com.pdyjak.powerampwear.music_browser.files.FilesBrowserFragment;
import com.pdyjak.powerampwear.music_browser.folders.FolderItem;
import com.pdyjak.powerampwear.music_browser.folders.FoldersBrowserFragment;
import com.pdyjak.powerampwearcommon.requests.GetAlbumsRequest;
import com.pdyjak.powerampwearcommon.requests.GetFilesRequest;
import com.pdyjak.powerampwearcommon.requests.PlaySongRequest;
import com.pdyjak.powerampwearcommon.requests.RequestsPaths;
import com.pdyjak.powerampwearcommon.responses.Parent;

public class LibraryExplorerFragment extends Fragment implements MusicLibraryNavigator.Listener {

    private static final String CATEGORY_FRAGMENT_TAG = "category";
    private static final String FILES_FRAGMENT_TAG = "files";
    private static final int BACK_BUTTON_ANIMATION_DURATION = 300;

    private MusicLibraryNavigator mMusicLibraryNavigator;
    private View mBackArrow;
    private boolean mBackArrowVisible;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.library_explorer, container, false);
        mBackArrow = view.findViewById(R.id.back_arrow);
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                if (fm.findFragmentByTag(CATEGORY_FRAGMENT_TAG) == null
                        && fm.getBackStackEntryCount() == 0) {
                    Fragment categoryFragment = new CategorySelectionFragment();
                    replaceFragmentImpl(categoryFragment, true, CATEGORY_FRAGMENT_TAG);
                    changeBackArrowVisibility(false);
                    return;
                }
                if (fm.getBackStackEntryCount() == 1) {
                    changeBackArrowVisibility(false);
                }
                fm.popBackStackImmediate();
            }
        });
        mMusicLibraryNavigator = ((App) getActivity().getApplicationContext())
                .getMusicLibraryNavigator();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (view == null) throw new IllegalStateException("Should never happen");
        Fragment fragment = new CategorySelectionFragment();
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment, CATEGORY_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMusicLibraryNavigator.addLibraryNavigationListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMusicLibraryNavigator.removeLibraryNavigationListener(this);
    }

    @Override
    public void onCategorySelected(@NonNull CategoryItem item, boolean fromPlayer,
                                   @Nullable String scrollTo) {
        switch (item.path) {
            case RequestsPaths.GET_FOLDERS:
                replaceFragment(new FoldersBrowserFragment(), false);
                break;

            case GetAlbumsRequest.PATH:
                startAlbumsBrowser(null, false);
                break;

            case RequestsPaths.GET_ARTISTS:
                replaceFragment(new ArtistsBrowserFragment(), false);
                break;

            case GetFilesRequest.PATH:
                startFilesBrowser(null, false, scrollTo);
                break;
        }
    }

    @Override
    public void onFolderSelected(@NonNull FolderItem item, boolean fromPlayer,
                                 @Nullable String scrollTo) {
        Parent parent = new Parent(item.id, Parent.Type.Folder);
        startFilesBrowser(parent, fromPlayer, scrollTo);
    }

    @Override
    public void onAlbumSelected(@NonNull AlbumItem item, boolean fromPlayer,
                                @Nullable String scrollTo) {
        Parent parent = new Parent(item.id, Parent.Type.Album);
        startFilesBrowser(parent, fromPlayer, scrollTo);
    }

    @Override
    public void onArtistSelected(@NonNull ArtistItem item, boolean fromPlayer) {
        Parent parent = new Parent(item.id, Parent.Type.Artist);
        startAlbumsBrowser(parent, fromPlayer);
    }

    private void startAlbumsBrowser(@Nullable Parent parent, boolean fromPlayer) {
        Bundle args = new Bundle();
        args.putParcelable(AlbumsBrowserFragment.PARENT_KEY, parent);
        Fragment fragment = new AlbumsBrowserFragment();
        fragment.setArguments(args);
        replaceFragment(fragment, fromPlayer);
    }

    private void startFilesBrowser(@Nullable Parent parent, boolean fromPlayer,
                                   @Nullable String scrollTo) {
        Bundle args = new Bundle();
        args.putParcelable(FilesBrowserFragment.PARENT_KEY, parent);
        args.putString(BrowserFragmentBase.SCROLL_DESTINATION_KEY, scrollTo);
        Fragment fragment = new FilesBrowserFragment();
        fragment.setArguments(args);
        replaceFragment(fragment, fromPlayer);
    }

    @Override
    public void onFileSelected(@NonNull FileItem item, boolean fromPlayer) {
        PlaySongRequest request = new PlaySongRequest(item.trackId, item.contextualId, item.parent);
        ((App) getActivity().getApplicationContext()).getMessageExchangeHelper().sendRequest(
                PlaySongRequest.PATH, request);
    }

    private void changeBackArrowVisibility(final boolean visible) {
        if (mBackArrowVisible == visible) return;
        mBackArrowVisible = visible;
        float start = visible ? 0f : 1f;
        float end = visible ? 1f : 0f;
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(BACK_BUTTON_ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mBackArrow.setScaleX(value);
                mBackArrow.setScaleY(value);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (visible) mBackArrow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!visible) mBackArrow.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private void replaceFragment(@NonNull Fragment fragment, boolean skipBackStack) {
        replaceFragmentImpl(fragment, skipBackStack, null);
        changeBackArrowVisibility(true);
    }

    private void replaceFragmentImpl(@NonNull Fragment fragment, boolean skipBackStack,
                                     @Nullable String tag) {
        // https://stackoverflow.com/questions/17148285/replacing-a-fragment-with-another-fragment-of-the-same-class
        // Android is shit
        FragmentManager fm = getChildFragmentManager();
        Fragment current = fm.findFragmentById(R.id.fragment_container);
        if (current != null && current.getClass() == fragment.getClass()
                && current instanceof BrowserFragmentBase) {
            Bundle currentArgs = current.getArguments();
            if (currentArgs != null) {
                Bundle newArgs = fragment.getArguments();
                currentArgs.putAll(newArgs);
            }
            ((BrowserFragmentBase) current).refresh();
            return;
        }
        if (skipBackStack) {
            while (fm.getBackStackEntryCount() > 0) fm.popBackStackImmediate();
        }
        FragmentTransaction transaction = fm.beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left,
                        R.animator.slide_in_left, R.animator.slide_out_right)
                .replace(R.id.fragment_container, fragment, tag);
        if (!skipBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }
}
