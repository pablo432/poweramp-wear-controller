package com.pdyjak.powerampwear

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private inner class ServiceConnectionImpl : ServiceConnection {
        private var mBinder: IBackgroundService? = null
        private var mShouldShowAlbumArt: Boolean? = null
        private var mShouldWakeWhenChangingSongs: Boolean? = null

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mBinder = IBackgroundService.Stub.asInterface(service)
            if (mShouldShowAlbumArt === null && mShouldWakeWhenChangingSongs === null) return
            try {
                if (mShouldShowAlbumArt !== null) {
                    mBinder!!.setShowAlbumArt(mShouldShowAlbumArt!!)
                    mShouldShowAlbumArt = null
                }
                if (mShouldWakeWhenChangingSongs !== null) {
                    mBinder!!.setWakeWhenChangingSongs(mShouldWakeWhenChangingSongs!!)
                    mShouldWakeWhenChangingSongs = null
                }
            } catch (e: RemoteException) {
                if (BuildConfig.DEBUG) e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mBinder = null
        }

        internal fun setShouldShowAlbumArt(shouldShow: Boolean) {
            if (mBinder === null) {
                mShouldShowAlbumArt = shouldShow
            } else {
                try {
                    mBinder!!.setShowAlbumArt(shouldShow)
                } catch (e: RemoteException) {
                    if (BuildConfig.DEBUG) e.printStackTrace()
                }
            }
        }

        internal fun setShouldWakeWhenChangingSongs(shouldWake: Boolean) {
            if (mBinder === null) {
                mShouldWakeWhenChangingSongs = shouldWake
            } else {
                try {
                    mBinder!!.setWakeWhenChangingSongs(shouldWake)
                } catch (e: RemoteException) {
                    if (BuildConfig.DEBUG) e.printStackTrace()
                }
            }
        }
    }

    private val mServiceConnection = ServiceConnectionImpl()
    private lateinit var mServiceIntent: Intent

    private var mServiceStarted: Boolean = false
    private var mServiceBound: Boolean = false
    private var mPowerampInstalledTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (BuildConfig.ENABLE_CRASH_REPORTING) {
            startService(Intent(this, WearableCrashInterceptorService::class.java))
        }
        mServiceIntent = Intent(this, BackgroundService::class.java)
        mPowerampInstalledTextView = findViewById(R.id.poweramp_installed_textview) as TextView

        findViewById(R.id.restart_service).setOnClickListener { restartService() }

        val albumArtCheckbox = findViewById(R.id.albumart_checkbox) as CheckBox
        albumArtCheckbox.isChecked = settingsManager.shouldShowAlbumArt
        albumArtCheckbox.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.shouldShowAlbumArt = isChecked
            mServiceConnection.setShouldShowAlbumArt(isChecked)
        }

        val wakelockCheckbox = findViewById(R.id.wakelock_checkbox) as CheckBox
        wakelockCheckbox.isChecked = settingsManager.shouldWakeWhenChangingSongs
        wakelockCheckbox.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.shouldWakeWhenChangingSongs = isChecked
            mServiceConnection.setShouldWakeWhenChangingSongs(isChecked)
        }

        findViewById(R.id.wakelock_text).setOnClickListener {
            wakelockCheckbox.isChecked = !wakelockCheckbox.isChecked
        }
    }

    private fun restartService() {
        if (!Utils.isPowerampInstalled(this)) {
            Toast.makeText(this, R.string.restart_service_pointless,
                    Toast.LENGTH_SHORT).show()
            return
        }
        stopService()
        startServiceIfNeeded()
        Toast.makeText(this, R.string.service_restarted,
                Toast.LENGTH_SHORT).show()
    }

    private fun startServiceIfNeeded() {
        if (mServiceStarted) {
            bindServiceIfNeeded()
            return
        }
        mServiceStarted = true
        startService(mServiceIntent)
        bindServiceIfNeeded()
    }

    private fun bindServiceIfNeeded() {
        if (mServiceBound) return
        mServiceBound = true
        bindService(mServiceIntent, mServiceConnection,
                Context.BIND_NOT_FOREGROUND or Context.BIND_ABOVE_CLIENT)
    }

    private fun unbindServiceIfNeeded() {
        if (!mServiceBound) return
        mServiceBound = false
        unbindService(mServiceConnection)
    }

    private fun stopService() {
        if (!mServiceStarted) return
        mServiceStarted = false
        unbindServiceIfNeeded()
        stopService(mServiceIntent)
    }

    override fun onResume() {
        super.onResume()
        val installed = Utils.isPowerampInstalled(this)
        if (installed) {
            mPowerampInstalledTextView!!.text = getString(R.string.just_launch_smartwatch)
            mPowerampInstalledTextView!!.setTextColor(Color.BLACK)
            startServiceIfNeeded()
        } else {
            mPowerampInstalledTextView!!.text = getString(R.string.poweramp_not_installed)
            mPowerampInstalledTextView!!.setTextColor(
                    ContextCompat.getColor(this, R.color.colorAccent))
            stopService()
        }
    }

    override fun onPause() {
        unbindServiceIfNeeded()
        super.onPause()
    }
}
