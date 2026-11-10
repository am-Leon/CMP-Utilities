package am.leon.utilities.helpers.appLanguage

import platform.Foundation.NSBundle

/**
 * The localization the app resolved to, which is what the user is actually reading.
 *
 * Not `NSLocale.preferredLanguages`: that is the device's wish list, so on a French phone running
 * an English/Arabic app it says `fr` - a language the app does not ship and cannot be showing.
 * `preferredLocalizations` intersects that list with `CFBundleLocalizations` first, and a per-app
 * language chosen in Settings reorders it the same way.
 */
actual fun currentAppLanguageTag(): String =
    (NSBundle.mainBundle.preferredLocalizations.firstOrNull() as? String).orEmpty()

/**
 * Unreachable by design: [rememberAppLanguageSettingsLauncher] never returns `null` on iOS, so an
 * app following it is sent to Settings and never arrives here. Throwing rather than doing nothing,
 * because a silent no-op looks exactly like a language that would not apply.
 */
actual fun setAppLanguage(language: IAppLanguage): Unit = throw UnsupportedOperationException(
    "iOS owns the app's language: send the user to the page " +
            "rememberAppLanguageSettingsLauncher() opens instead of setting ${language.tag} here."
)
