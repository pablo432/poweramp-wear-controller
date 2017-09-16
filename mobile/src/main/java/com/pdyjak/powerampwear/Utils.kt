package com.pdyjak.powerampwear

import android.content.Context

object Utils {
    fun isPowerampInstalled(context: Context): Boolean {
        val packageName = context.getString(R.string.poweramp_package_name)
        return context.packageManager.isApplicationInstalled(packageName)
    }
}
