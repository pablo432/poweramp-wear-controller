package com.pdyjak.powerampwear.onboarding

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.Utils

class InstallationVerificationFragment : Fragment() {

    private var mAppInstalledTextView: TextView? = null
    private var mBigIcon: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(
                R.layout.onboarding_installation_verification, container, false
        )
        mAppInstalledTextView = view.findViewById(R.id.app_detected_textview) as TextView
        mBigIcon = view.findViewById(R.id.big_icon)
        return view
    }

    override fun onResume() {
        super.onResume()
        if (Utils.isPowerampInstalled(context)) {
            mAppInstalledTextView!!.setText(R.string.installation_detected)
            mBigIcon!!.setOnClickListener(null)
        } else {
            mAppInstalledTextView!!.setText(R.string.poweramp_not_installed_onboarding)
            mBigIcon!!.setOnClickListener { openGooglePlay() }
        }
    }

    private fun openGooglePlay() {
        val packageName = getString(R.string.poweramp_package_name)
        try {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName)))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)))
        }

    }

}
