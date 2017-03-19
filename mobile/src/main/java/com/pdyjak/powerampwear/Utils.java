package com.pdyjak.powerampwear;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

public class Utils {
    public static boolean isPowerampInstalled(@NonNull Context context) {
        String packageName = context.getString(R.string.poweramp_package_name);
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
