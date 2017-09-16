package com.pdyjak.powerampwear

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.pdyjak.powerampwear.onboarding.OnboardingActivity

class ActivityLauncher : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, if (settingsManager.onboardingCompleted)
            MainActivity::class.java else OnboardingActivity::class.java)
        startActivity(intent)
        finish()
    }
}