package com.pdyjak.powerampwear.music_browser;

import android.app.Fragment;
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

    private MusicLibraryNavigator mMusicLibraryNavigator;
    private View mBackArrow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.library_explorer, container, false);
        mBackArrow = view.findViewById(R.id.back_arrow);
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getChildFragmentManager().getBackStackEntryCount() == 1) {
                    mBackArrow.setVisibility(View.GONE);
                }
                getChildFragmentManager().popBackStack();
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
                .add(R.id.fragment_container, fragment)
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
    public void onCategorySelected(@NonNull CategoryItem item) {
        switch (item.path) {
            case RequestsPaths.GET_FOLDERS:
                replaceFragment(new FoldersBrowserFragment());
                break;

            case GetAlbumsRequest.PATH:
                startAlbumsBrowser(null);
                break;

            case RequestsPaths.GET_ARTISTS:
                replaceFragment(new ArtistsBrowserFragment());
                break;

            case GetFilesRequest.PATH:
                startFilesBrowser(null);
                break;
        }
    }

    @Override
    public void onFolderSelected(@NonNull FolderItem item) {
        Parent parent = new Parent(item.id, Parent.Type.Folder);
        startFilesBrowser(parent);
    }

    @Override
    public void onAlbumSelected(@NonNull AlbumItem item) {
        Parent parent = new Parent(item.id, Parent.Type.Album);
        startFilesBrowser(parent);
    }

    @Override
    public void onArtistSelected(@NonNull ArtistItem item) {
        Parent parent = new Parent(item.id, Parent.Type.Artist);
        startAlbumsBrowser(parent);
    }

    private void startAlbumsBrowser(@Nullable Parent parent) {
        Bundle args = new Bundle();
        args.putParcelable(AlbumsBrowserFragment.PARENT_KEY, parent);
        Fragment fragment = new AlbumsBrowserFragment();
        fragment.setArguments(args);
        replaceFragment(fragment);
    }

    private void startFilesBrowser(@Nullable Parent parent) {
        Bundle args = new Bundle();
        args.putParcelable(FilesBrowserFragment.PARENT_KEY, parent);
        Fragment fragment = new FilesBrowserFragment();
        fragment.setArguments(args);
        replaceFragment(fragment);
    }

    @Override
    public void onFileSelected(@NonNull FileItem item) {
        PlaySongRequest request = new PlaySongRequest(item.trackId, item.parent);
        ((App) getActivity().getApplicationContext()).getMessageExchangeHelper().sendRequest(
                PlaySongRequest.PATH, request);
    }

    private void replaceFragment(@NonNull Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left,
                        R.animator.slide_in_left, R.animator.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        mBackArrow.setVisibility(View.VISIBLE);
    }
}
