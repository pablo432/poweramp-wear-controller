package com.pdyjak.powerampwear.onboarding;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class OnboardingViewPagerAdapter extends FragmentPagerAdapter {
    static final int PAGES_COUNT = 3;

    OnboardingViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new WelcomeFragment();

            case 1:
                return new InstallationVerificationFragment();

            case 2:
                return new AlmostReadyFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGES_COUNT;
    }
}
