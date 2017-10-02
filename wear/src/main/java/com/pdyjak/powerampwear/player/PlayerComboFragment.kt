package com.pdyjak.powerampwear.player

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.common.ambientModeStateProvider
import com.pdyjak.powerampwear.common.byId
import com.pdyjak.powerampwear.common.messageExchangeHelper
import com.pdyjak.powerampwear.common.musicLibraryNavigator
import com.pdyjak.powerampwear.common.settingsManager
import com.pdyjak.powerampwear.custom_views.SmoothScrollingLinearLayoutManager

class PlayerComboFragment : Fragment() {

    private inner class Views(view: View, viewModel: PlayerViewModel) {
        private val mRecyclerView: RecyclerView = view byId R.id.recycler_view
        private val mSnapHelper: SnapHelper = LinearSnapHelper()

        init {
            mRecyclerView.layoutManager = SmoothScrollingLinearLayoutManager(activity)
            mRecyclerView.adapter = MainRecyclerViewAdapter(viewModel)
            mSnapHelper.attachToRecyclerView(mRecyclerView)
            mRecyclerView.scrollToPosition(1)
        }

        fun destroy() {
            mSnapHelper.attachToRecyclerView(null)
            mRecyclerView.adapter = null
            mRecyclerView.layoutManager = null
        }
    }

    private var mViews: Views? = null
    private var mPlayerViewModel: PlayerViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPlayerViewModel = PlayerViewModel(activity.settingsManager,
                activity.messageExchangeHelper, activity.ambientModeStateProvider,
                activity.musicLibraryNavigator)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.player_combo_view, container, false)
        mViews = Views(view, mPlayerViewModel!!)
        return view
    }

    override fun onResume() {
        super.onResume()
        mPlayerViewModel!!.onResume()
    }

    override fun onPause() {
        mPlayerViewModel!!.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        mViews!!.destroy()
        mViews = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        mPlayerViewModel = null
        super.onDestroy()
    }
}
