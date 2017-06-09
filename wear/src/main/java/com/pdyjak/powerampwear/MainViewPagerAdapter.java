package com.pdyjak.powerampwear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.pdyjak.powerampwear.music_browser.LibraryExplorerFragment;
import com.pdyjak.powerampwear.player.PlayerComboFragment;
import com.pdyjak.powerampwear.settings.SettingsFragment;

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PlayerComboFragment();

            case 1:
                return new LibraryExplorerFragment();

            case 2:
                return new SettingsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
