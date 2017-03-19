package com.pdyjak.powerampwear.onboarding;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.pdyjak.powerampwear.App;
import com.pdyjak.powerampwear.MainActivity;
import com.pdyjak.powerampwear.R;

import me.relex.circleindicator.CircleIndicator;

public class OnboardingActivity extends AppCompatActivity {

    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @NonNull
        private final ArgbEvaluator mEvaluator = new ArgbEvaluator();
        @NonNull
        private final int[] mColors = new int[OnboardingViewPagerAdapter.PAGES_COUNT];

        private PageChangeListener() {
            Context context = OnboardingActivity.this;
            mColors[0] = ContextCompat.getColor(context,
                    R.color.onboarding_welcome_screen_bg);
            mColors[1] = ContextCompat.getColor(context,
                    R.color.onboarding_installation_verification_screen_bg);
            mColors[2] = ContextCompat.getColor(context,
                    R.color.onboarding_almost_ready_screen_bg);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position + 1 >= mColors.length) return;
            int color = (int) mEvaluator.evaluate(
                    positionOffset, mColors[position], mColors[position + 1]
            );
            mRoot.setBackgroundColor(color);
        }

        @Override
        public void onPageSelected(int position) {
            mCurrentPage = position;
            if (position == OnboardingViewPagerAdapter.PAGES_COUNT - 1) {
                mNextButton.setText(getString(R.string.onboarding_finish));
            } else {
                mNextButton.setText(getString(R.string.onboarding_next));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mCurrentPage == OnboardingViewPagerAdapter.PAGES_COUNT - 1) {
                ((App) getApplicationContext()).saveOnboardingCompleted();
                Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                mViewPager.setCurrentItem(mCurrentPage + 1);
            }
        }
    }

    private int mCurrentPage;
    private PageChangeListener mPageChangeListener;
    private View mRoot;
    private TextView mNextButton;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        mRoot = findViewById(R.id.root);
        mPageChangeListener = new PageChangeListener();
        mNextButton = (TextView) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new ButtonClickListener());
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mViewPager.setAdapter(new OnboardingViewPagerAdapter(getSupportFragmentManager()));
        CircleIndicator dotsIndicator = (CircleIndicator) findViewById(R.id.dots_indicator);
        dotsIndicator.setViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(mPageChangeListener);
    }
}
