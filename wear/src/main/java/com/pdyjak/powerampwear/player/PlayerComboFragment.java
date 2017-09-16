package com.pdyjak.powerampwear.player;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pdyjak.powerampwear.App;
import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwear.custom_views.BlockingRecyclerView;
import com.pdyjak.powerampwear.custom_views.SmoothScrollingLinearLayoutManager;
import com.pdyjak.powerampwear.settings.SettingsManager;

public class PlayerComboFragment extends android.app.Fragment
        implements PlayerViewModel.CommonEventsListener {

    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private PlayerViewModel mPlayerViewModel;
    private BlockingRecyclerView mRecyclerView;
    private boolean mFirstResume = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App app = (App) getActivity().getApplicationContext();
        mPlayerViewModel = new PlayerViewModel(app.getSettingsManager(),
                app.getMessageExchangeHelper(), app,
                app.getMusicLibraryNavigator());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayerViewModel = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_combo_view, container, false);
        mRecyclerView = (BlockingRecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new SmoothScrollingLinearLayoutManager(getActivity()));
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(new RecyclerViewAdapter(mPlayerViewModel));
        mRecyclerView.scrollToPosition(1);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView.setAdapter(null);
        mRecyclerView = null;
    }

    private void showRepeatShuffleButtonsMovedOnboarding() {
        mRecyclerView.setBlocked(true);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRecyclerView == null) return;
                mRecyclerView.smoothScrollToPosition(0);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Context context = getActivity();
                        if (context == null || mRecyclerView == null) return;
                        Toast toast = Toast.makeText(context, R.string.repeat_shuffle_moved_hint,
                                Toast.LENGTH_SHORT);
                        toast.show();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showVolumeControlsOnboarding();
                            }
                        }, 3000);
                    }
                }, 600);
            }
        }, 500);
    }

    private void showVolumeControlsOnboarding() {
        if (mRecyclerView == null) return;
        mRecyclerView.smoothScrollToPosition(1);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Context context = getActivity();
                if (context == null || mRecyclerView == null) return;
                Toast toast = Toast.makeText(context, R.string.volume_controls_added_hint,
                        Toast.LENGTH_SHORT);
                toast.show();
                mRecyclerView.setBlocked(false);
                ((App) getActivity().getApplicationContext()).getSettingsManager()
                        .saveVolumeControlsOnboardingShown();
            }
        }, 600);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFirstResume) {
            mFirstResume = false;
            SettingsManager settingsManager = ((App) getActivity().getApplicationContext())
                    .getSettingsManager();
            if (!settingsManager.volumeControlsOnboardingShown()) {
                mPlayerViewModel.addListenerWeakly(this);
            }
        }
        mPlayerViewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayerViewModel.removeListener(this);
        mPlayerViewModel.onPause();
    }

    @Override
    public void onStateChanged() {
        if (mPlayerViewModel.getCurrentState() != PlayerViewModel.State.Ok) return;
        mPlayerViewModel.removeListener(this);
        showRepeatShuffleButtonsMovedOnboarding();
    }

    @Override
    public void onPauseChanged() {
    }

    @Override
    public void onTrackInfoChanged() {
    }
}
