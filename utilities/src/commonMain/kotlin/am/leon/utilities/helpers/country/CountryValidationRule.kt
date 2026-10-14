package am.leon.utilities.helpers.country

import am.leon.utilities.extentions.arabicNumbersToEn

data class CountryValidationRule(
    val countryCode: String,
    val dialCode: String,
    val nationalPrefix: String?,
    val phonePattern: String?,
    val phoneLengths: List<Int>,
    val phoneExample: String?,
    val postalPattern: String?,
    val postalExample: String?,
    val isPostalRequired: Boolean,
    val hasPostalSystem: Boolean?,
) {

    val phoneRegex: Regex? by lazy(LazyThreadSafetyMode.PUBLICATION) {
        phonePattern?.let(::Regex)
    }

    val postalRegex: Regex? by lazy(LazyThreadSafetyMode.PUBLICATION) {
        postalPattern?.let { Regex(it, RegexOption.IGNORE_CASE) }
    }

    val minPhoneLength: Int? get() = phoneLengths.minOrNull()

    val maxPhoneLength: Int? get() = phoneLengths.maxOrNull()

    /**
     * Whether [phoneNumber] is a valid number for this country.
     *
     * Tolerates how people actually type numbers: Arabic-Indic digits, spaces,
     * dashes and brackets, a leading trunk prefix (`0512345678`), a leading
     * dial code (`+966512345678`), or both. Every form is tried, so a number
     * the pattern accepts is never rejected over presentation.
     */
    fun matchesPhoneNumber(phoneNumber: String?): Boolean {
        val regex = phoneRegex ?: return false
        return phoneNumberCandidates(phoneNumber).any(regex::matches)
    }

    fun matchesPostalCode(postalCode: String?): Boolean {
        val regex = postalRegex ?: return false
        val normalized = postalCode?.trim()?.arabicNumbersToEn() ?: return false
        return normalized.isNotEmpty() && regex.matches(normalized)
    }

    /**
     * The national number behind [phoneNumber], which is the form the backend
     * expects to receive.
     *
     * Returns whichever reading of the input this country's pattern accepts, so
     * a number typed with its dial code, its trunk prefix or separators is sent
     * as the bare national number rather than verbatim. When nothing matches,
     * the bare digits are returned unchanged: an invalid number is validation's
     * to reject, not this method's to reshape.
     */
    fun nationalPhoneNumber(phoneNumber: String?): String {
        val regex = phoneRegex
        return phoneNumberCandidates(phoneNumber).firstOrNull { candidate ->
            regex?.matches(candidate) == true
        } ?: normalizePhoneNumber(phoneNumber)
    }

    /**
     * The digits of [phoneNumber], with Arabic-Indic numerals folded to ASCII
     * and every separator removed.
     */
    fun normalizePhoneNumber(phoneNumber: String?): String {
        return phoneNumber?.arabicNumbersToEn()?.filter(Char::isDigit).orEmpty()
    }

    /**
     * Every reading of [phoneNumber] worth testing against [phoneRegex].
     *
     * Peels the prefixes a person might have typed, outermost first: the
     * international access code, then the dial code, then the trunk prefix — so
     * `00966501234567`, `+966501234567`, `0501234567` and `501234567` all
     * reduce to the same national number.
     *
     * Each prefix is only ever *tried*, never assumed. Stripping one outright
     * would corrupt the countries whose numbers legitimately begin with their
     * own trunk prefix: a Kazakh number really can start with `8`, which is
     * also Kazakhstan's national prefix.
     */
    private fun phoneNumberCandidates(phoneNumber: String?): Set<String> {
        val normalized = normalizePhoneNumber(phoneNumber)
        if (normalized.isEmpty()) return emptySet()

        val candidates = mutableSetOf(normalized)
        listOf(INTERNATIONAL_ACCESS_PREFIX, dialCode, nationalPrefix).forEach { prefix ->
            if (prefix.isNullOrEmpty()) return@forEach
            candidates += candidates.mapNotNull { candidate ->
                candidate.removePrefix(prefix)
                    .takeIf { candidate.startsWith(prefix) && it.isNotEmpty() }
            }
        }
        return candidates
    }

    private companion object {
        const val INTERNATIONAL_ACCESS_PREFIX = "00"
    }
}