package com.pdyjak.powerampwear.player;

import android.content.Context;
import android.graphics.drawable.LevelListDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdyjak.powerampwear.BuildConfig;
import com.pdyjak.powerampwear.R;

class RepeatShuffleSwitcherViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, PlayerViewModel.RepeatShuffleModesListener {

    private static final int REPEAT_TAG = 0;
    private static final int SHUFFLE_TAG = 1;

    @NonNull
    private final PlayerViewModel mViewModel;
    @NonNull
    private final LevelListDrawable mRepeatButtonDrawable;
    @NonNull
    private final LevelListDrawable mShuffleButtonDrawable;
    @NonNull
    private final TextView mRepeatTextView;
    @NonNull
    private final TextView mShuffleTextView;

    RepeatShuffleSwitcherViewHolder(@NonNull View view, @NonNull PlayerViewModel viewModel) {
        super(view);
        mViewModel = viewModel;
        mViewModel.addListenerWeakly(this);

        View repeatButton = view.findViewById(R.id.repeat_container);
        ImageView repeatImage = (ImageView) view.findViewById(R.id.repeat_button);
        repeatButton.setTag(REPEAT_TAG);
        repeatButton.setOnClickListener(this);
        mRepeatButtonDrawable = (LevelListDrawable) repeatImage.getDrawable();
        mRepeatTextView = (TextView) view.findViewById(R.id.repeat_mode);

        View shuffleButton = view.findViewById(R.id.shuffle_container);
        ImageView shuffleImage = (ImageView) view.findViewById(R.id.shuffle_button);
        shuffleButton.setTag(SHUFFLE_TAG);
        shuffleButton.setOnClickListener(this);
        mShuffleButtonDrawable = (LevelListDrawable) shuffleImage.getDrawable();
        mShuffleTextView = (TextView) view.findViewById(R.id.shuffle_mode);

        onRepeatModeChanged();
        onShuffleModeChanged();
    }

    @Override
    public void onRepeatModeChanged() {
        PlayerViewModel.RepeatMode mode = mViewModel.getRepeatMode();
        mRepeatButtonDrawable.setLevel(mode.value);
        Context context = itemView.getContext();
        String str;
        switch (mode) {
            case Off:
                str = context.getString(R.string.repeat_off);
                break;

            case List:
                str = context.getString(R.string.repeat_list);
                break;

            case Song:
                str = context.getString(R.string.repeat_song);
                break;

            case AdvanceList:
                str = context.getString(R.string.advance_list);
                break;

            default:
                if (BuildConfig.DEBUG) throw new IllegalArgumentException();
                return;
        }
        mRepeatTextView.setText(str);
    }

    @Override
    public void onShuffleModeChanged() {
        PlayerViewModel.ShuffleMode mode = mViewModel.getShuffleMode();
        mShuffleButtonDrawable.setLevel(mode.value);
        Context context = itemView.getContext();
        String str;
        switch (mode) {
            case Off:
                str = context.getString(R.string.shuffle_off);
                break;

            case All:
                str = context.getString(R.string.shuffle_all);
                break;

            case Lists:
                str = context.getString(R.string.shuffle_lists);
                break;

            case Songs:
                str = context.getString(R.string.shuffle_songs);
                break;

            case SongsLists:
                str = context.getString(R.string.shuffle_songs_lists);
                break;

            default:
                if (BuildConfig.DEBUG) throw new IllegalArgumentException();
                return;
        }
        mShuffleTextView.setText(str);
    }

    @Override
    public void onClick(View v) {
        switch ((int) v.getTag()) {
            case REPEAT_TAG:
                mViewModel.toggleRepeatMode();
                break;

            case SHUFFLE_TAG:
                mViewModel.toggleShuffleMode();
                break;
        }
    }
}
