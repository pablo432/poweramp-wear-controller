package com.pdyjak.powerampwear;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.wearable.MessageEvent;
import com.pdyjak.powerampwear.music_browser.files.FileItem;
import com.pdyjak.powerampwearcommon.events.AlbumArtChangedEvent;
import com.pdyjak.powerampwearcommon.events.TrackChangedEvent;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends WearableActivity implements MessageListener {

    public static final int DOTS_VISIBILITY_TIMEOUT = 1000;
    public static final int OFFSCREEN_PAGE_LIMIT = 2;

    private class MusicBrowserListener extends MusicBrowserListenerAdapter {
        @Override
        public void onFileSelected(@NonNull FileItem item) {
            mShouldNavigateToPlayerView = true;
        }
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (mHideDotsIndicatorRunnable == null) {
                    mHideDotsIndicatorRunnable = new Runnable() {
                        @Override
                        public void run() {
                            mDotsIndicator.setVisibility(View.GONE);
                        }
                    };
                    mHandler.postDelayed(mHideDotsIndicatorRunnable, DOTS_VISIBILITY_TIMEOUT);
                }
            } else {
                if (mHideDotsIndicatorRunnable != null) {
                    mHandler.removeCallbacks(mHideDotsIndicatorRunnable);
                    mHideDotsIndicatorRunnable = null;
                }
                mDotsIndicator.setVisibility(View.VISIBLE);
            }
        }
    }

    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    @NonNull
    private final MusicBrowserListener mMusicBrowserListener = new MusicBrowserListener();

    private ImageView mAlbumArt;
    private Bitmap mAlbumArtBitmap;
    private boolean mShouldNavigateToPlayerView;
    private ViewPager mViewPager;
    private CircleIndicator mDotsIndicator;
    private Runnable mHideDotsIndicatorRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAmbientEnabled();
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new MainViewPagerAdapter(getFragmentManager()));
        mViewPager.addOnPageChangeListener(new PageChangeListener());
        mViewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
        mAlbumArt = (ImageView) findViewById(R.id.album_art);
        mDotsIndicator = (CircleIndicator) findViewById(R.id.dots_indicator);
        mDotsIndicator.setViewPager(mViewPager);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        updateDisplay();
    }

    @Override
    protected void onResume() {
        App app = (App) getApplicationContext();
        app.onResume();
        app.getMessageExchangeHelper().addMessageListenerWeakly(this);
        app.getMusicLibraryNavigator().addLibraryNavigationListener(mMusicBrowserListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        App app = (App) getApplicationContext();
        app.onPause();
        app.getMessageExchangeHelper().removeMessageListener(this);
        app.getMusicLibraryNavigator().removeLibraryNavigationListener(mMusicBrowserListener);
        super.onPause();
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (AlbumArtChangedEvent.PATH.equals(messageEvent.getPath())) {
            byte[] bytes = messageEvent.getData();
            if (bytes == null) {
                mAlbumArtBitmap = null;
            } else {
                mAlbumArtBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
            updateDisplay();
        } else if (TrackChangedEvent.PATH.equals(messageEvent.getPath())
                && mShouldNavigateToPlayerView) {
            mShouldNavigateToPlayerView = false;
            mViewPager.setCurrentItem(0, true);
        }
    }

    private void updateDisplay() {
        mAlbumArt.setImageBitmap(isAmbient() ? null : mAlbumArtBitmap);
    }
}
