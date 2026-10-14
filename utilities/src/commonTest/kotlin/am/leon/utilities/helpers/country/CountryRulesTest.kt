package am.leon.utilities.helpers.country

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.text.get

class CountryRulesTest {

    /**
     * Test cases for [CountryRules.normalizeDialCode]
     * - Given a dial code in any notation when invoke normalizeDialCode then return bare digits
     * - Given Arabic-Indic digits when invoke normalizeDialCode then return ASCII digits
     * - Given a blank or non-numeric dial code when invoke normalizeDialCode then return null
     */
    @Test
    fun givenDialCodeInAnyNotation_whenInvokeNormalizeDialCode_thenReturnBareDigits() {
        assertEquals("966", CountryRules.normalizeDialCode("00966"))
        assertEquals("966", CountryRules.normalizeDialCode("+966"))
        assertEquals("966", CountryRules.normalizeDialCode("966"))
        assertEquals("966", CountryRules.normalizeDialCode(" 966 "))
        assertEquals("1", CountryRules.normalizeDialCode("001"))
    }

    @Test
    fun givenArabicIndicDigits_whenInvokeNormalizeDialCode_thenReturnAsciiDigits() {
        assertEquals("966", CountryRules.normalizeDialCode("٩٦٦"))
    }

    @Test
    fun givenBlankDialCode_whenInvokeNormalizeDialCode_thenReturnNull() {
        assertNull(CountryRules.normalizeDialCode(null))
        assertNull(CountryRules.normalizeDialCode(""))
        assertNull(CountryRules.normalizeDialCode("   "))
        assertNull(CountryRules.normalizeDialCode("+"))
        assertNull(CountryRules.normalizeDialCode("00"))
    }

    /**
     * Test cases for [CountryRules] lookups
     * - Given a country code when invoke forCountryCode then return its rule
     * - Given an unknown country code when invoke forCountryCode then return null
     * - Given a unique dial code when invoke forDialCode then return one rule
     * - Given a shared dial code when invoke forDialCode then return every territory main country first
     * - Given an unknown dial code when invoke forDialCode then return empty list
     */
    @Test
    fun givenCountryCode_whenInvokeForCountryCode_thenReturnItsRule() {
        val rule = CountryRules.forCountryCode("SA")
        assertNotNull(rule)
        assertEquals("SA", rule.countryCode)
        assertEquals("966", rule.dialCode)
        assertEquals("0", rule.nationalPrefix)
    }

    @Test
    fun givenLowercaseCountryCode_whenInvokeForCountryCode_thenReturnItsRule() {
        assertEquals("GB", CountryRules.forCountryCode("gb")?.countryCode)
        assertEquals("GB", CountryRules.forCountryCode(" Gb ")?.countryCode)
    }

    @Test
    fun givenUnknownCountryCode_whenInvokeForCountryCode_thenReturnNull() {
        assertNull(CountryRules.forCountryCode("ZZ"))
        assertNull(CountryRules.forCountryCode(null))
        assertNull(CountryRules.forCountryCode(""))
        assertNull(CountryRules.forCountryCode("SAU"))
    }

    @Test
    fun givenUniqueDialCode_whenInvokeForDialCode_thenReturnOneRule() {
        val rules = CountryRules.forDialCode("00966")
        assertEquals(1, rules.size)
        assertEquals("SA", rules.first().countryCode)
    }

    @Test
    fun givenSharedDialCode_whenInvokeForDialCode_thenReturnEveryTerritoryMainCountryFirst() {
        val nanp = CountryRules.forDialCode("+1")
        assertTrue(nanp.size > 1, "expected the NANP to cover several territories")
        assertEquals("US", nanp.first().countryCode, "main country must come first")
        assertTrue(nanp.any { it.countryCode == "CA" })

        val uk = CountryRules.forDialCode("44")
        assertEquals("GB", uk.first().countryCode)
        assertTrue(uk.map { it.countryCode }.containsAll(listOf("GB", "GG", "IM", "JE")))
    }

    @Test
    fun givenUnknownDialCode_whenInvokeForDialCode_thenReturnEmptyList() {
        assertTrue(CountryRules.forDialCode("99999").isEmpty())
        assertTrue(CountryRules.forDialCode(null).isEmpty())
        assertTrue(CountryRules.forDialCode("").isEmpty())
    }

    /**
     * Test cases for [CountryValidationRule.matchesPhoneNumber]
     * - Given a valid national number when invoke matchesPhoneNumber then return true
     * - Given a number with a trunk prefix when invoke matchesPhoneNumber then return true
     * - Given a number with its dial code when invoke matchesPhoneNumber then return true
     * - Given a number with separators when invoke matchesPhoneNumber then return true
     * - Given Arabic-Indic digits when invoke matchesPhoneNumber then return true
     * - Given a number that legitimately starts with the trunk prefix when invoke matchesPhoneNumber then return true
     * - Given a too short number when invoke matchesPhoneNumber then return false
     * - Given a blank number when invoke matchesPhoneNumber then return false
     */
    @Test
    fun givenValidNationalNumber_whenInvokeMatchesPhoneNumber_thenReturnTrue() {
        val saudiArabia = requireNotNull(CountryRules.forCountryCode("SA"))
        assertTrue(saudiArabia.matchesPhoneNumber("501234567"))
    }

    @Test
    fun givenNumberWithTrunkPrefix_whenInvokeMatchesPhoneNumber_thenReturnTrue() {
        val saudiArabia = requireNotNull(CountryRules.forCountryCode("SA"))
        assertTrue(saudiArabia.matchesPhoneNumber("0501234567"))
    }

    @Test
    fun givenNumberWithDialCode_whenInvokeMatchesPhoneNumber_thenReturnTrue() {
        val saudiArabia = requireNotNull(CountryRules.forCountryCode("SA"))
        assertTrue(saudiArabia.matchesPhoneNumber("966501234567"))
        assertTrue(saudiArabia.matchesPhoneNumber("+966501234567"))
        assertTrue(saudiArabia.matchesPhoneNumber("00966501234567"))
    }

    @Test
    fun givenNumberWithSeparators_whenInvokeMatchesPhoneNumber_thenReturnTrue() {
        val saudiArabia = requireNotNull(CountryRules.forCountryCode("SA"))
        assertTrue(saudiArabia.matchesPhoneNumber("50 123 4567"))
        assertTrue(saudiArabia.matchesPhoneNumber("(50) 123-4567"))
    }

    @Test
    fun givenArabicIndicDigits_whenInvokeMatchesPhoneNumber_thenReturnTrue() {
        val saudiArabia = requireNotNull(CountryRules.forCountryCode("SA"))
        assertTrue(saudiArabia.matchesPhoneNumber("٥٠١٢٣٤٥٦٧"))
    }

    @Test
    fun givenNumberStartingWithTrunkPrefix_whenInvokeMatchesPhoneNumber_thenReturnTrue() {
        val unitedKingdom = requireNotNull(CountryRules.forCountryCode("GB"))
        assertEquals("0", unitedKingdom.nationalPrefix)
        assertTrue(unitedKingdom.matchesPhoneNumber("7400123456"), "the national number")
        assertTrue(unitedKingdom.matchesPhoneNumber("07400123456"), "as it is dialled at home")

        val kazakhstan = requireNotNull(CountryRules.forCountryCode("KZ"))
        assertEquals("8", kazakhstan.nationalPrefix)
        assertTrue(kazakhstan.matchesPhoneNumber("7710009998"))
        assertFalse(kazakhstan.matchesPhoneNumber("8710009998"))
    }

    @Test
    fun givenTooShortNumber_whenInvokeMatchesPhoneNumber_thenReturnFalse() {
        val saudiArabia = requireNotNull(CountryRules.forCountryCode("SA"))
        assertFalse(saudiArabia.matchesPhoneNumber("12345"))
        assertFalse(saudiArabia.matchesPhoneNumber("301234567"))
    }

    @Test
    fun givenBlankNumber_whenInvokeMatchesPhoneNumber_thenReturnFalse() {
        val saudiArabia = requireNotNull(CountryRules.forCountryCode("SA"))
        assertFalse(saudiArabia.matchesPhoneNumber(null))
        assertFalse(saudiArabia.matchesPhoneNumber(""))
        assertFalse(saudiArabia.matchesPhoneNumber("abc"))
    }

    @Test
    fun givenNumberNoNumberTypeAccepts_whenInvokeMatchesPhoneNumber_thenReturnFalse() {
        val egypt = requireNotNull(CountryRules.forCountryCode("EG"))
        listOf("1001234567", "1101234567", "1201234567", "1501234567").forEach { mobile ->
            assertTrue(egypt.matchesPhoneNumber(mobile), "$mobile is a real mobile")
        }
        assertFalse(egypt.matchesPhoneNumber("1999999999"), "no mobile block starts 19")
        assertFalse(egypt.matchesPhoneNumber("123456789"), "nine digits behind a 1")
        assertFalse(egypt.matchesPhoneNumber("12345678"), "eight digits behind a 1")
        assertFalse(egypt.matchesPhoneNumber("234567890"), "a Cairo landline")
        assertFalse(egypt.matchesPhoneNumber("31234567"), "an Alexandria landline")
    }

    /**
     * Test cases for [CountryValidationRule.nationalPhoneNumber]
     * - Given a number in any notation when invoke nationalPhoneNumber then return the national number
     * - Given an unmatched number when invoke nationalPhoneNumber then return its bare digits
     */
    @Test
    fun givenNumberInAnyNotation_whenInvokeNationalPhoneNumber_thenReturnTheNationalNumber() {
        // Whatever the field tolerates has to leave as one form, or validation
        // passes locally and the backend rejects the payload.
        val egypt = requireNotNull(CountryRules.forCountryCode("EG"))
        assertEquals("1001234567", egypt.nationalPhoneNumber("1001234567"))
        assertEquals("1001234567", egypt.nationalPhoneNumber("01001234567"))
        assertEquals("1001234567", egypt.nationalPhoneNumber("+201001234567"))
        assertEquals("1001234567", egypt.nationalPhoneNumber("00201001234567"))
        assertEquals("1001234567", egypt.nationalPhoneNumber("010 0123 4567"))
        assertEquals("1001234567", egypt.nationalPhoneNumber("٠١٠٠١٢٣٤٥٦٧"))
    }

    @Test
    fun givenUnmatchedNumber_whenInvokeNationalPhoneNumber_thenReturnItsBareDigits() {
        val egypt = requireNotNull(CountryRules.forCountryCode("EG"))
        assertEquals("12345", egypt.nationalPhoneNumber("12345"))
        assertEquals("", egypt.nationalPhoneNumber(null))
    }

    /**
     * Test cases for [CountryValidationRule.matchesPostalCode]
     * - Given an alphanumeric postal code when invoke matchesPostalCode then return true
     * - Given a postal code in either case when invoke matchesPostalCode then return true
     * - Given a country with several formats when invoke matchesPostalCode then accept each
     * - Given a country with no postal system when invoke matchesPostalCode then return false
     * - Given an invalid postal code when invoke matchesPostalCode then return false
     */
    @Test
    fun givenAlphanumericPostalCode_whenInvokeMatchesPostalCode_thenReturnTrue() {
        assertTrue(requireNotNull(CountryRules.forCountryCode("GB")).matchesPostalCode("SW1A 1AA"))
        assertTrue(requireNotNull(CountryRules.forCountryCode("GB")).matchesPostalCode("SW1A1AA"))
        assertTrue(requireNotNull(CountryRules.forCountryCode("CA")).matchesPostalCode("K1A 0B1"))
        assertTrue(requireNotNull(CountryRules.forCountryCode("NL")).matchesPostalCode("1234 AB"))
    }

    @Test
    fun givenPostalCodeInEitherCase_whenInvokeMatchesPostalCode_thenReturnTrue() {
        val unitedKingdom = requireNotNull(CountryRules.forCountryCode("GB"))
        assertTrue(unitedKingdom.matchesPostalCode("sw1a 1aa"))
    }

    @Test
    fun givenCountryWithSeveralFormats_whenInvokeMatchesPostalCode_thenAcceptEach() {
        val egypt = requireNotNull(CountryRules.forCountryCode("EG"))
        assertTrue(egypt.matchesPostalCode("4460232"))
        assertTrue(egypt.matchesPostalCode("12345"))
        assertFalse(egypt.matchesPostalCode("123456"))
    }

    @Test
    fun givenCountryWithNoPostalSystem_whenInvokeMatchesPostalCode_thenReturnFalse() {
        val unitedArabEmirates = requireNotNull(CountryRules.forCountryCode("AE"))
        assertEquals(false, unitedArabEmirates.hasPostalSystem)
        assertNull(unitedArabEmirates.postalPattern)
        assertFalse(unitedArabEmirates.matchesPostalCode("12345"))
    }

    @Test
    fun givenInvalidPostalCode_whenInvokeMatchesPostalCode_thenReturnFalse() {
        val saudiArabia = requireNotNull(CountryRules.forCountryCode("SA"))
        assertTrue(saudiArabia.matchesPostalCode("11564"))
        assertFalse(saudiArabia.matchesPostalCode("1234"))
        assertFalse(saudiArabia.matchesPostalCode("SW1A 1AA"))
        assertFalse(saudiArabia.matchesPostalCode(null))
    }

    /**
     * Test cases for the generated [countryValidationRules] table
     * - Given the table when read then contain every country keyed by its own ISO code
     * - Given the table when read then agree with the dial code index
     * - Given every phone example when validated against its own rule then be accepted
     * - Given every postal example when validated against its own rule then be accepted
     * - Given a country absent from the postal data when read then report an unknown postal system
     */
    @Test
    fun givenTable_whenRead_thenContainEveryCountryKeyedByItsOwnCountryCode() {
        assertTrue(
            countryValidationRules.size > 200,
            "expected the whole world, found ${countryValidationRules.size}"
        )
        countryValidationRules.forEach { (key, rule) ->
            assertEquals(key, rule.countryCode, "$key is keyed by the wrong ISO code")
            assertTrue(rule.dialCode.isNotEmpty(), "${rule.countryCode} has no dial code")
            assertTrue(rule.dialCode.all(Char::isDigit), "${rule.countryCode} has a malformed dial code")
        }
    }

    @Test
    fun givenTable_whenRead_thenAgreeWithTheDialCodeIndex() {
        dialCodeToCountryCodes.forEach { (dialCode, isoCodes) ->
            assertTrue(isoCodes.isNotEmpty(), "+$dialCode indexes no country")
            isoCodes.forEach { countryCode ->
                val rule = countryValidationRules[countryCode]
                assertNotNull(rule, "+$dialCode indexes the unknown country $countryCode")
                assertEquals(dialCode, rule.dialCode, "$countryCode is indexed under the wrong dial code")
            }
        }
        countryValidationRules.values.forEach { rule ->
            assertEquals(
                dialCodeToCountryCodes[rule.dialCode]?.contains(rule.countryCode),
                true,
                "${rule.countryCode} is missing from the dial code index"
            )
        }
    }

    @Test
    fun givenEveryPhoneExample_whenValidatedAgainstItsOwnRule_thenBeAccepted() {
        countryValidationRules.values.forEach { rule ->
            val example = rule.phoneExample ?: return@forEach
            assertTrue(
                rule.matchesPhoneNumber(example),
                "${rule.countryCode} rejects its own example number $example"
            )
            assertTrue(
                example.length in rule.phoneLengths,
                "${rule.countryCode} example $example has a length outside ${rule.phoneLengths}"
            )
        }
    }

    @Test
    fun givenEveryPostalExample_whenValidatedAgainstItsOwnRule_thenBeAccepted() {
        countryValidationRules.values.forEach { rule ->
            val example = rule.postalExample ?: return@forEach
            assertTrue(
                rule.matchesPostalCode(example),
                "${rule.countryCode} rejects its own example postal code $example"
            )
        }
    }

    @Test
    fun givenTable_whenRead_thenUseNoShorthandDigitClass() {
        val offenders = countryValidationRules.values.filter { rule ->
            listOfNotNull(rule.phonePattern, rule.postalPattern).any { it.contains("\\d") }
        }
        assertTrue(
            offenders.isEmpty(),
            "regenerate the rules table: ${offenders.map { it.countryCode }} still use \\d"
        )
    }

    @Test
    fun givenCountryWithNoMobileNumbers_whenRead_thenReportNoPhonePattern() {
        val tristanDaCunha = requireNotNull(CountryRules.forCountryCode("TA"))
        assertNull(tristanDaCunha.phonePattern)
        assertTrue(tristanDaCunha.phoneLengths.isEmpty())
        assertFalse(tristanDaCunha.matchesPhoneNumber("8999"), "a real fixed line, still not a mobile")
    }

    @Test
    fun givenCountryAbsentFromPostalData_whenRead_thenReportAnUnknownPostalSystem() {
        val syria = requireNotNull(CountryRules.forCountryCode("SY"))
        assertNull(syria.hasPostalSystem)
        assertNull(syria.postalPattern)
    }

    @Test
    fun givenCountryWithCuratedPostalData_whenRead_thenReportItsFormat() {
        val malaysia = requireNotNull(CountryRules.forCountryCode("MY"))
        assertEquals(true, malaysia.hasPostalSystem)
        assertEquals("50450", malaysia.postalExample)
        assertTrue(malaysia.matchesPostalCode("50450"))
        assertTrue(malaysia.matchesPostalCode("10450"))
        assertTrue(malaysia.matchesPostalCode("88000"))
        assertFalse(malaysia.matchesPostalCode("1234"))
        assertFalse(malaysia.matchesPostalCode("504500"))
    }
}