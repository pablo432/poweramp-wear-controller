package com.pdyjak.powerampwear;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.pdyjak.powerampwear.onboarding.OnboardingActivity;

public class ActivityLauncher extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean onboardingCompleted = ((App) getApplicationContext()).isOnboardingCompleted();
        Intent intent;
        if (onboardingCompleted) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, OnboardingActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
