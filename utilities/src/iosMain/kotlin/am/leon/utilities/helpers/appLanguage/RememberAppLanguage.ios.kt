package am.leon.utilities.helpers.appLanguage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/** iOS relaunches the app when its language changes in Settings, so it is read once per launch. */
@Composable
actual fun rememberAppLanguageTag(): String = remember { currentAppLanguageTag() }
