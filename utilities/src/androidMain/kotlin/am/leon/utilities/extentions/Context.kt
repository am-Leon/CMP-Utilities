package am.leon.utilities.extentions

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

fun Context.isAppInForeground(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = activityManager.runningAppProcesses ?: return false

    for (appProcess in appProcesses) {
        if (appProcess.processName == packageName && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            return true
    }
    return false
}

fun Context.isScreenOn(): Boolean {
    val manager = getSystemService(Context.POWER_SERVICE) as PowerManager
    return manager.isInteractive
}

// -------------------------------------------- System ---------------------------------------------

fun Context.getPackageInfo(packageName: String): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    else
        packageManager.getPackageInfo(packageName, 0)

fun Context.getApplicationVersionName(): String? = getPackageInfo(packageName).versionName

fun Context.buildSettingNavigationIntent(): Intent {
    return Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
}
