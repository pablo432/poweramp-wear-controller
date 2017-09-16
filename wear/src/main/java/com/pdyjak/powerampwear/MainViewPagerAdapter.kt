package com.pdyjak.powerampwear

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentPagerAdapter

import com.pdyjak.powerampwear.music_browser.LibraryExplorerFragment
import com.pdyjak.powerampwear.player.PlayerComboFragment
import com.pdyjak.powerampwear.settings.SettingsFragment

class MainViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return PlayerComboFragment()
            1 -> return LibraryExplorerFragment()
            2 -> return SettingsFragment()
        }
        return null
    }

    override fun getCount(): Int {
        return 3
    }
}
