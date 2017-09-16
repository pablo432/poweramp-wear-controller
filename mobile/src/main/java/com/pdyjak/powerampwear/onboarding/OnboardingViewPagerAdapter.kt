package com.pdyjak.powerampwear.onboarding

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

internal class OnboardingViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    companion object {
        const val PAGES_COUNT = 3
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return WelcomeFragment()
            1 -> return InstallationVerificationFragment()
            2 -> return AlmostReadyFragment()
        }
        return null
    }

    override fun getCount(): Int {
        return PAGES_COUNT
    }
}
