package am.leon.utilities.helpers.country

import am.leon.utilities.extentions.arabicNumbersToEn


/**
 * Looks up [CountryValidationRule]s, tolerant of whichever identifier the
 * caller happens to hold.
 *
 * Backends identify countries inconsistently — an ISO 3166-1 alpha-2 code on
 * an address, a dial code on a phone field — so either form resolves to the
 * same rule instead of forcing callers to convert first.
 */
object CountryRules {

    /** The rule for an ISO 3166-1 alpha-2 code such as `"SA"`, case-insensitive. */
    fun forCountryCode(countryCode: String?): CountryValidationRule? {
        val key = countryCode?.trim()?.uppercase()?.takeIf { it.length == 2 } ?: return null
        return countryValidationRules[key]
    }

    /**
     * Every rule sharing [dialCode], main country first, or empty when the code
     * is unrecognised.
     *
     * A list rather than a single rule because a dial code is not a country:
     * `+1` covers the 25 NANP territories and `+44` covers GB, GG, IM and JE.
     * A number valid in any one of them has to be accepted, so phone validation
     * has to see them all.
     */
    fun forDialCode(dialCode: String?): List<CountryValidationRule> {
        val key = normalizeDialCode(dialCode) ?: return emptyList()
        return dialCodeToCountryCodes[key]?.mapNotNull { countryValidationRules[it] }.orEmpty()
    }

    /**
     * Reduces a dial code to bare digits: `"00966"`, `"+966"` and `"966"` all
     * become `"966"`. Returns `null` when nothing usable is left.
     *
     * Leading zeros are safe to drop because no country calling code begins
     * with one; they are only ever the international access prefix.
     */
    fun normalizeDialCode(dialCode: String?): String? {
        return dialCode
            ?.arabicNumbersToEn()
            ?.filter(Char::isDigit)
            ?.trimStart('0')
            ?.takeIf { it.isNotEmpty() }
    }
}