package am.leon.utilities.helpers.appLanguage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Keyed on the configuration, which covers both ways the language can change: an app that declares
 * `configChanges="locale|layoutDirection"` is handed a new one without being recreated, and an app
 * that does not is recreated and composes again from scratch.
 */
@Composable
actual fun rememberAppLanguageTag(): String {
    val configuration = LocalConfiguration.current
    return remember(configuration) { currentAppLanguageTag() }
}
