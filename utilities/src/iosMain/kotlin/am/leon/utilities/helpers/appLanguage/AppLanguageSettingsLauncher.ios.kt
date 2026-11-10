package am.leon.utilities.helpers.appLanguage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

/**
 * Never `null`: every iOS version lists the app's `CFBundleLocalizations` on its page in Settings,
 * so the app never needs a picker of its own.
 */
@Composable
actual fun rememberAppLanguageSettingsLauncher(): (() -> Unit)? = remember {
    {
        NSURL.URLWithString(UIApplicationOpenSettingsURLString)?.let { url ->
            // The one-argument openURL is refused since iOS 18 ("Force returning false").
            UIApplication.sharedApplication.openURL(
                url, options = emptyMap<Any?, Any>(), completionHandler = null
            )
        }
    }
}
