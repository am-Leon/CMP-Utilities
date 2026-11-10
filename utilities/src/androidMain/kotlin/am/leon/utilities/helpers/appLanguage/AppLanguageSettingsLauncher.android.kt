package am.leon.utilities.helpers.appLanguage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
private fun hasSystemPage(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

/**
 * Android 13 gave every app a language page in Settings. Below that there is none, so this returns
 * `null` and the app shows its own picker.
 */
@Composable
actual fun rememberAppLanguageSettingsLauncher(): (() -> Unit)? {
    val context = LocalContext.current

    return remember(context) {
        if (hasSystemPage().not()) return@remember null

        { context.openAppLanguageSettings() }
    }
}

/**
 * `packageName` is the running application's id, never this library's.
 *
 * A library has no package of its own at runtime: `getPackageName()` is a method on the [Context]
 * instance, which belongs to the hosting app's process, and it answers with that app's
 * `applicationId` - suffixes and all, so a `.debug` build points at the `.debug` entry, which is
 * the one actually installed. The library's own `namespace` is a compile-time name for its R class
 * and reaches nothing here.
 */
private fun Context.openAppLanguageSettings() {
    val appPackage = Uri.fromParts("package", packageName, null)

    // Present since 13, but some OEM builds ship without it; the app's details page always exists
    // and has the language entry on it.
    runCatching { startActivity(Intent(Settings.ACTION_APP_LOCALE_SETTINGS, appPackage)) }
        .onFailure {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, appPackage))
        }
}
