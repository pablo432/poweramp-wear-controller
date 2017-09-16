package com.pdyjak.powerampwear.player

import android.graphics.drawable.LevelListDrawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.pdyjak.powerampwear.R

internal class RepeatShuffleSwitcherViewHolder(view: View, private val mViewModel: PlayerViewModel)
    : RecyclerView.ViewHolder(view), PlayerViewModel.RepeatShuffleModesListener {

    private val mRepeatButtonDrawable: LevelListDrawable
    private val mShuffleButtonDrawable: LevelListDrawable
    private val mRepeatTextView: TextView
    private val mShuffleTextView: TextView

    init {
        mViewModel.addListenerWeakly(this)

        val repeatButton = view.findViewById(R.id.repeat_container)
        val repeatImage = view.findViewById(R.id.repeat_button) as ImageView
        repeatButton.setOnClickListener { mViewModel.toggleRepeatMode() }
        mRepeatButtonDrawable = repeatImage.drawable as LevelListDrawable
        mRepeatTextView = view.findViewById(R.id.repeat_mode) as TextView

        val shuffleButton = view.findViewById(R.id.shuffle_container)
        val shuffleImage = view.findViewById(R.id.shuffle_button) as ImageView
        shuffleButton.setOnClickListener { mViewModel.toggleShuffleMode() }
        mShuffleButtonDrawable = shuffleImage.drawable as LevelListDrawable
        mShuffleTextView = view.findViewById(R.id.shuffle_mode) as TextView

        onRepeatModeChanged()
        onShuffleModeChanged()
    }

    override fun onRepeatModeChanged() {
        val mode = mViewModel.repeatMode
        mRepeatButtonDrawable.level = mode.value
        val context = itemView.context
        val str: String
        when (mode) {
            PlayerViewModel.RepeatMode.Off -> str = context.getString(R.string.repeat_off)
            PlayerViewModel.RepeatMode.List -> str = context.getString(R.string.repeat_list)
            PlayerViewModel.RepeatMode.Song -> str = context.getString(R.string.repeat_song)
            PlayerViewModel.RepeatMode.AdvanceList -> str = context.getString(R.string.advance_list)
        }
        mRepeatTextView.text = str
    }

    override fun onShuffleModeChanged() {
        val mode = mViewModel.shuffleMode
        mShuffleButtonDrawable.level = mode.value
        val context = itemView.context
        val str: String
        when (mode) {
            PlayerViewModel.ShuffleMode.Off -> str = context.getString(R.string.shuffle_off)
            PlayerViewModel.ShuffleMode.All -> str = context.getString(R.string.shuffle_all)
            PlayerViewModel.ShuffleMode.Lists -> str = context.getString(R.string.shuffle_lists)
            PlayerViewModel.ShuffleMode.Songs -> str = context.getString(R.string.shuffle_songs)
            PlayerViewModel.ShuffleMode.SongsLists ->
                str = context.getString(R.string.shuffle_songs_lists)
        }
        mShuffleTextView.text = str
    }
}
