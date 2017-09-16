package com.pdyjak.powerampwear.settings

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.musicLibraryCache
import com.pdyjak.powerampwear.settingsManager

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)

        (view.findViewById(R.id.circular_scrolling_checkbox) as CheckBox).let {
            it.isChecked = activity.settingsManager.useCircularScrollingGesture()
            it.setOnCheckedChangeListener { _, isChecked ->
                activity.settingsManager.saveUseCircularScrolling(isChecked) }
        }

        (view.findViewById(R.id.clock_checkbox) as CheckBox).let {
            it.isChecked = activity.settingsManager.shouldShowClock()
            it.setOnCheckedChangeListener { _, isChecked ->
                activity.settingsManager.saveShowClock(isChecked) }
        }

        view.findViewById(R.id.invalidate_cache).setOnClickListener {
            activity.musicLibraryCache.invalidate()
            Toast.makeText(activity, R.string.cache_invalidated, Toast.LENGTH_SHORT).show()
        }
        return view
    }
}
