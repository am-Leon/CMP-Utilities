package am.leon.utilities.helpers.appLanguage

import androidx.compose.runtime.Composable

/**
 * [currentAppLanguageTag] as composition state: it changes when the user switches the app
 * language, from the system settings or inside the app, so effects keyed on it run again.
 */
@Composable
expect fun rememberAppLanguageTag(): String

/**
 * The one of [supported] the app is being shown in, as composition state. `null` when the platform
 * reports a language the app does not ship - see [currentAppLanguage].
 */
@Composable
fun <Language : IAppLanguage> rememberAppLanguage(supported: Iterable<Language>): Language? =
    supported.matching(rememberAppLanguageTag())
