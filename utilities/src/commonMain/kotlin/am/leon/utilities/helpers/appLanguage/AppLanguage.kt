package am.leon.utilities.helpers.appLanguage

/**
 * One language the app can be shown in.
 *
 * An interface, not a closed set: this library ships [AppLanguage] because almost every product
 * here is English and Arabic, but an app with sixteen declares its own and everything below keeps
 * working. [tag] is what has to line up with the platform, so it is also the only thing matched on.
 *
 * @param tag BCP-47, as it appears in `res/values-<tag>` or `CFBundleLocalizations`: `en`, `ar`,
 * and a region or script only where the app really ships them apart - `pt-BR`, `zh-Hans`.
 * @param nativeName the language's name in itself - `English`, `العربية` - so a picker reads the
 * same whichever language the app is currently in.
 */
interface IAppLanguage {
    val tag: String
    val nativeName: String
}

/** The two nearly every app here ships. An app with its own set does not use this. */
enum class AppLanguage(override val tag: String, override val nativeName: String) : IAppLanguage {
    ENGLISH(tag = "en", nativeName = "English"),
    ARABIC(tag = "ar", nativeName = "العربية")
}

/**
 * The one of these whose [IAppLanguage.tag] the platform is reporting, or `null` when none is.
 *
 * The platform's tag and the app's rarely agree on precision, so this is RFC 4647 lookup: drop one
 * subtag off the platform's tag at a time and take the first app tag that matches exactly. A device
 * on `en-US` finds an app shipping `en`; `zh-Hans-CN` finds `zh-Hans`; `ar-KW` finds `ar`.
 *
 * Only then does it widen the other way, for a device reporting less than the app ships - plain
 * `pt` against an app that only has `pt-BR`. That pass gives up rather than guesses when more than
 * one candidate shares the subtag: `zh` against both `zh-Hans` and `zh-Hant` has no right answer,
 * and picking either would show a reader of one script the other.
 *
 * **Shipping regional variants?** Ship the plain language too. With `ar`, `ar-EG` and `ar-SA` a
 * Kuwaiti device lands on `ar`; with only the two regions it matches nothing, because nothing here
 * can know which of them a Kuwaiti reader would rather have.
 */
fun <Language : IAppLanguage> Iterable<Language>.matching(tag: String): Language? {
    var candidate = tag.lowercase()

    while (true) {
        firstOrNull { it.tag.lowercase() == candidate }?.let { return it }

        val lastSubtag = candidate.lastIndexOf('-')
        if (lastSubtag < 0) break
        candidate = candidate.substring(startIndex = 0, endIndex = lastSubtag)
    }

    val primary = tag.lowercase().substringBefore('-')
    return singleOrNull { it.tag.lowercase().substringBefore('-') == primary }
}

/**
 * The one of [supported] the app is being shown in, or `null` when the platform is reporting a
 * language the app does not ship - which happens whenever the device language is one of the
 * other 7000. The caller picks the fallback, because which language an app falls back to is the
 * app's decision, not this library's.
 */
fun <Language : IAppLanguage> currentAppLanguage(supported: Iterable<Language>): Language? =
    supported.matching(currentAppLanguageTag())

/**
 * The language the app is currently showing, as a BCP-47 tag, read live: the user can change it
 * from outside the app as well as inside it.
 */
expect fun currentAppLanguageTag(): String

/**
 * Applies [language] in place, for the platforms that have no settings page of their own.
 *
 * Only reachable when [rememberAppLanguageSettingsLauncher] returned `null`; where it returns a
 * launcher, the system owns this and calling it here throws.
 *
 * **Android 12 and below need one line of app setup for the choice to survive a cold start.** It
 * applies immediately either way, so a missing setup looks like it worked until the next launch.
 * See `docs/app-language.md`, "autoStoreLocales".
 */
expect fun setAppLanguage(language: IAppLanguage)
