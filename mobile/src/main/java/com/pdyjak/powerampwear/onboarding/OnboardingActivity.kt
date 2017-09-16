package com.pdyjak.powerampwear.onboarding

import android.animation.ArgbEvaluator
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView

import com.pdyjak.powerampwear.MainActivity
import com.pdyjak.powerampwear.R
import com.pdyjak.powerampwear.settingsManager

import me.relex.circleindicator.CircleIndicator

class OnboardingActivity : AppCompatActivity() {

    private inner class PageChangeListener : ViewPager.OnPageChangeListener {
        private val mEvaluator = ArgbEvaluator()
        private val mColors = IntArray(OnboardingViewPagerAdapter.PAGES_COUNT)

        init {
            val context = this@OnboardingActivity
            mColors[0] = ContextCompat.getColor(context,
                    R.color.onboarding_welcome_screen_bg)
            mColors[1] = ContextCompat.getColor(context,
                    R.color.onboarding_installation_verification_screen_bg)
            mColors[2] = ContextCompat.getColor(context,
                    R.color.onboarding_almost_ready_screen_bg)
        }

        override fun onPageScrolled(position: Int,
                                    positionOffset: Float,
                                    positionOffsetPixels: Int) {
            if (position + 1 >= mColors.size) return
            val color = mEvaluator.evaluate(
                    positionOffset, mColors[position], mColors[position + 1]
            ) as Int
            mRoot!!.setBackgroundColor(color)
        }

        override fun onPageSelected(position: Int) {
            mCurrentPage = position
            if (position == OnboardingViewPagerAdapter.PAGES_COUNT - 1) {
                mNextButton!!.text = getString(R.string.onboarding_finish)
            } else {
                mNextButton!!.text = getString(R.string.onboarding_next)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            if (mCurrentPage == OnboardingViewPagerAdapter.PAGES_COUNT - 1) {
                settingsManager.onboardingCompleted = true
                val intent = Intent(this@OnboardingActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                mViewPager!!.currentItem = mCurrentPage + 1
            }
        }
    }

    private lateinit var mPageChangeListener: PageChangeListener
    private var mRoot: View? = null
    private var mNextButton: TextView? = null
    private var mViewPager: ViewPager? = null
    private var mCurrentPage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        mPageChangeListener = PageChangeListener()
        mRoot = findViewById(R.id.root)
        mNextButton = findViewById(R.id.next_button) as TextView
        mNextButton!!.setOnClickListener(ButtonClickListener())
        mViewPager = findViewById(R.id.view_pager) as ViewPager
        mViewPager!!.addOnPageChangeListener(mPageChangeListener)
        mViewPager!!.adapter = OnboardingViewPagerAdapter(supportFragmentManager)
        val dotsIndicator = findViewById(R.id.dots_indicator) as CircleIndicator
        dotsIndicator.setViewPager(mViewPager)
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewPager!!.removeOnPageChangeListener(mPageChangeListener)
    }
}
