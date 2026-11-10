package am.leon.utilities.helpers.appLanguage

import androidx.compose.runtime.Composable

/**
 * Opens the system's per-app language page, or `null` where there is none - Android 12 and below.
 *
 * The nullability is the whole API: a launcher means the system owns the choice and the app only
 * has to offer the way in, `null` means the app shows its own picker over the languages it ships
 * and applies one with [setAppLanguage].
 *
 * ```
 * val openSettings = rememberAppLanguageSettingsLauncher()
 * if (openSettings != null) LanguageRow(onClick = openSettings)
 * else LanguagePicker(AppLanguage.entries, onPick = ::setAppLanguage)
 * ```
 *
 * The picker is deliberately not in here. It is a screen, it has to match the app's design, and
 * the library does not know how many languages the app ships.
 *
 * **Both system pages are built from app setup this library cannot do**: `android:localeConfig` on
 * Android 13+, `CFBundleLocalizations` on iOS. Without it the page opens empty, which looks like
 * this function is broken rather than like a manifest is short a line. See `docs/app-language.md`.
 */
@Composable
expect fun rememberAppLanguageSettingsLauncher(): (() -> Unit)?
