package com.pdyjak.powerampwear.player

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pdyjak.powerampwear.R

internal class MainRecyclerViewAdapter(private val mViewModel: PlayerViewModel)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val REPEAT_SHUFFLE_SWITCHER = 0
        private val PLAYER = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val view: View
        when (viewType) {
            REPEAT_SHUFFLE_SWITCHER -> {
                view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.repeat_shuffle_switcher, parent, false)
                return RepeatShuffleSwitcherViewHolder(view, mViewModel)
            }

            PLAYER -> {
                view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.player_view, parent, false)
                return PlayerViewHolder(view, mViewModel)
            }
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun getItemViewType(position: Int): Int {
        return position // ( ͡° ͜ʖ ͡°)
    }

    override fun getItemCount() = 2
}
