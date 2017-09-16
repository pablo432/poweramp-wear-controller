package com.pdyjak.powerampwear.player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdyjak.powerampwear.App;
import com.pdyjak.powerampwear.R;
import com.pdyjak.powerampwear.custom_views.SmoothScrollingLinearLayoutManager;

public class PlayerComboFragment extends android.app.Fragment {

    private PlayerViewModel mPlayerViewModel;
    private RecyclerView mRecyclerView;

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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
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

    @Override
    public void onResume() {
        super.onResume();
        mPlayerViewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayerViewModel.onPause();
    }
}
