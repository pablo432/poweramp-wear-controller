package com.pdyjak.powerampwear.player;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdyjak.powerampwear.R;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEMS_COUNT = 2;

    private static final int REPEAT_SHUFFLE_SWITCHER = 0;
    private static final int PLAYER = 1;

    @NonNull
    private final PlayerViewModel mViewModel;

    RecyclerViewAdapter(@NonNull PlayerViewModel viewModel) {
        mViewModel = viewModel;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case REPEAT_SHUFFLE_SWITCHER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.repeat_shuffle_switcher, parent, false);
                return new RepeatShuffleSwitcherViewHolder(view, mViewModel);

            case PLAYER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.player_view, parent, false);
                return new PlayerViewHolder(view, mViewModel);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemViewType(int position) {
        return position; // ( ͡° ͜ʖ ͡°)
    }

    @Override
    public int getItemCount() {
        return ITEMS_COUNT;
    }
}
