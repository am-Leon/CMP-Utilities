package am.leon.utilities.helpers.appLanguage

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** What an app with its own set declares - the sixteen-language case, not the shipped pair. */
private data class Language(
    override val tag: String, override val nativeName: String
) : IAppLanguage

private val PORTUGUESE_BRAZIL = Language("pt-BR", "Português (Brasil)")
private val PORTUGUESE = Language("pt", "Português")
private val CHINESE_SIMPLIFIED = Language("zh-Hans", "简体中文")
private val CHINESE_TRADITIONAL = Language("zh-Hant", "繁體中文")
private val FRENCH = Language("fr", "Français")
private val ARABIC = Language("ar", "العربية")
private val ARABIC_EGYPT = Language("ar-EG", "العربية (مصر)")
private val ARABIC_SAUDI = Language("ar-SA", "العربية (السعودية)")

/**
 * The platform's tag and the app's rarely agree on precision, and everything downstream - which
 * strings load, which way the layout runs - follows from this one match.
 */
class AppLanguageTest {

    @Test
    fun the_shipped_pair_is_tagged_the_way_the_platforms_expect() {
        assertEquals("en", AppLanguage.ENGLISH.tag)
        assertEquals("ar", AppLanguage.ARABIC.tag)
        assertEquals("العربية", AppLanguage.ARABIC.nativeName, "named in itself, not in English")
    }

    @Test
    fun an_exact_tag_matches() {
        assertEquals(AppLanguage.ARABIC, AppLanguage.entries.matching("ar"))
    }

    @Test
    fun case_does_not_decide_it() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.entries.matching("EN"))
        assertEquals(PORTUGUESE_BRAZIL, listOf(PORTUGUESE_BRAZIL).matching("pt-br"))
    }

    @Test
    fun a_device_region_falls_back_to_the_plain_language_the_app_ships() {
        // en-US, ar-EG: the common case, and the app ships only `en` and `ar`.
        assertEquals(AppLanguage.ENGLISH, AppLanguage.entries.matching("en-US"))
        assertEquals(AppLanguage.ARABIC, AppLanguage.entries.matching("ar-EG"))
    }

    @Test
    fun a_plain_device_tag_finds_the_one_regional_language_the_app_ships() {
        assertEquals(PORTUGUESE_BRAZIL, listOf(PORTUGUESE_BRAZIL, FRENCH).matching("pt"))
    }

    @Test
    fun an_exact_match_beats_a_subtag_one() {
        val supported = listOf(PORTUGUESE, PORTUGUESE_BRAZIL)

        assertEquals(PORTUGUESE_BRAZIL, supported.matching("pt-BR"))
        assertEquals(PORTUGUESE, supported.matching("pt"))
    }

    @Test
    fun an_ambiguous_subtag_gives_up_rather_than_guessing() {
        // Showing a reader of one Chinese script the other is worse than falling back.
        assertNull(listOf(CHINESE_SIMPLIFIED, CHINESE_TRADITIONAL).matching("zh"))
        assertEquals(
            CHINESE_SIMPLIFIED,
            listOf(CHINESE_SIMPLIFIED, CHINESE_TRADITIONAL).matching("zh-Hans")
        )
    }

    @Test
    fun an_app_can_ship_a_language_per_region() {
        val supported = listOf(ARABIC, ARABIC_EGYPT, ARABIC_SAUDI)

        assertEquals(ARABIC_EGYPT, supported.matching("ar-EG"))
        assertEquals(ARABIC_SAUDI, supported.matching("ar-SA"))
        assertEquals(ARABIC, supported.matching("ar"))
    }

    @Test
    fun a_region_the_app_does_not_ship_falls_back_to_the_plain_language() {
        // A Kuwaiti device gets Arabic, not English, because the app also ships plain `ar`.
        assertEquals(ARABIC, listOf(ARABIC, ARABIC_EGYPT, ARABIC_SAUDI).matching("ar-KW"))
    }

    @Test
    fun regions_without_a_plain_language_to_fall_back_on_match_nothing() {
        // Nothing here can know whether a Kuwaiti reader would rather have Egyptian or Saudi
        // Arabic, so the app ships `ar` as well - which is what the KDoc tells it to do.
        assertNull(listOf(ARABIC_EGYPT, ARABIC_SAUDI).matching("ar-KW"))
    }

    @Test
    fun a_script_survives_while_the_region_is_dropped() {
        // zh-Hans-CN -> zh-Hans, rather than collapsing all the way to an ambiguous zh.
        assertEquals(
            CHINESE_SIMPLIFIED,
            listOf(CHINESE_SIMPLIFIED, CHINESE_TRADITIONAL).matching("zh-Hans-CN")
        )
    }

    @Test
    fun a_language_the_app_does_not_ship_is_null_not_a_default() {
        // The caller picks the fallback; which one is the app's decision, not the library's.
        assertNull(AppLanguage.entries.matching("fr"))
        assertNull(emptyList<IAppLanguage>().matching("en"))
    }

    @Test
    fun an_app_s_own_set_works_the_same_at_sixteen() {
        val sixteen = listOf(
            "en", "ar", "fr", "de", "es", "it", "pt-BR", "ru",
            "tr", "fa", "ur", "hi", "id", "ms", "zh-Hans", "ja"
        ).map { Language(tag = it, nativeName = it) }

        assertEquals("ur", sixteen.matching("ur")?.tag)
        assertEquals("pt-BR", sixteen.matching("pt")?.tag, "the only pt it ships")
        assertEquals("ja", sixteen.matching("ja-JP")?.tag)
        assertNull(sixteen.matching("sw"))
    }
}
