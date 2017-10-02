package com.pdyjak.powerampwear.player

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.common.ambientModeStateProvider
import com.pdyjak.powerampwear.common.messageExchangeHelper
import com.pdyjak.powerampwear.common.musicLibraryNavigator
import com.pdyjak.powerampwear.common.settingsManager
import com.pdyjak.powerampwear.custom_views.SmoothScrollingLinearLayoutManager

class PlayerComboFragment : Fragment() {

    private var mPlayerViewModel: PlayerViewModel? = null
    private var mRecyclerView: RecyclerView? = null
    private var mSnapHelper: LinearSnapHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPlayerViewModel = PlayerViewModel(activity.settingsManager,
                activity.messageExchangeHelper, activity.ambientModeStateProvider,
                activity.musicLibraryNavigator)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayerViewModel = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.player_combo_view, container, false)
        mRecyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        mRecyclerView!!.layoutManager = SmoothScrollingLinearLayoutManager(activity)
        mSnapHelper = LinearSnapHelper()
        mSnapHelper!!.attachToRecyclerView(mRecyclerView)
        mRecyclerView!!.adapter = MainRecyclerViewAdapter(mPlayerViewModel!!)
        mRecyclerView!!.scrollToPosition(1)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRecyclerView!!.adapter = null
        mSnapHelper!!.attachToRecyclerView(null)
    }

    override fun onResume() {
        super.onResume()
        mPlayerViewModel!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPlayerViewModel!!.onPause()
    }
}
