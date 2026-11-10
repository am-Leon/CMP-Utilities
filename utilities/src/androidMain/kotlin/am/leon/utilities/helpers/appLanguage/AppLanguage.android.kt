package am.leon.utilities.helpers.appLanguage

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

/**
 * The per-app locale set through AppCompat or the system settings, else the device default.
 *
 * The full tag rather than just the language: `en-US` and `pt-BR` carry a region the app may or
 * may not ship separately, and [matching] is what decides which - it cannot if the region is
 * thrown away here.
 */
actual fun currentAppLanguageTag(): String =
    (AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()).toLanguageTag()

/**
 * AppCompat applies the locale immediately. Whether it is also *persisted* is the app's to arrange,
 * and only below API 33: the manifest needs AppCompat's `AppLocalesMetadataHolderService` carrying
 * `autoStoreLocales="true"`, or the choice is gone on the next cold start.
 *
 * Not to be confused with `generateLocaleConfig`, which is a different setting for a different
 * problem - it builds the list the Android 13+ system page shows, and does nothing for persistence.
 * Both are in `docs/app-language.md`.
 */
actual fun setAppLanguage(language: IAppLanguage) =
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))
