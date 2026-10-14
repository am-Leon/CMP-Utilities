package am.leon.utilities.common.domain.models.request

import kotlinx.datetime.LocalDate
import am.leon.utilities.helpers.country.CountryRules
import am.leon.utilities.helpers.inputField.IInputFieldError
import am.leon.utilities.helpers.inputField.InputFieldError
import am.leon.utilities.helpers.inputField.InputFieldPhoneError
import am.leon.utilities.helpers.inputField.InputFieldPostalCodeError
import am.leon.utilities.helpers.inputField.InputFieldStringCountError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidatorTest {

    /**
     * Test cases for [Validator.General]
     * - Given valid required flag when invoke validateIsRequired then return empty set for true
     * - Given valid required flag when invoke validateIsRequired then return required error for false
     * - Given valid initial state when invoke validateIsInitialState then return initial state error for true
     * - Given valid initial state when invoke validateIsInitialState then return empty set for false
     */
    @Test
    fun givenValidRequiredFlagTrue_whenInvokeValidateIsRequired_thenReturnEmptySet() {
        val isRequired = true
        val result = Validator.General.validateIsRequired(isRequired)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenValidRequiredFlagFalse_whenInvokeValidateIsRequired_thenReturnRequiredError() {
        val isRequired = false
        val result = Validator.General.validateIsRequired(isRequired)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenValidInitialStateTrue_whenInvokeValidateIsInitialState_thenReturnInitialStateError() {
        val isInitial = true
        val result = Validator.General.validateIsInitialState(isInitial)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.INITIAL_STATE))
    }

    @Test
    fun givenValidInitialStateFalse_whenInvokeValidateIsInitialState_thenReturnEmptySet() {
        val isInitial = false
        val result = Validator.General.validateIsInitialState(isInitial)
        assertTrue(result.isEmpty())
    }

    /**
     * Test cases for [Validator.Numbers]
     * - Given valid positive number when invoke validatePositive then return success
     * - Given null value and required when invoke validatePositive then return required error
     * - Given null value and not required when invoke validatePositive then return success
     * - Given zero value when invoke validatePositive then return positive error
     * - Given negative number when invoke validatePositive then return positive error
     * - Given valid positive number when invoke validatePositiveOrZero then return success
     * - Given zero value when invoke validatePositiveOrZero then return success
     * - Given negative number when invoke validatePositiveOrZero then return positive or zero error
     * - Given null value and required when invoke validatePositiveOrZero then return required error
     * - Given valid non-zero string when invoke validateNonZero then return success
     * - Given zero string when invoke validateNonZero then return zero not accepted error
     * - Given zero string with spaces when invoke validateNonZero then return zero not accepted error
     * - Given null value and required when invoke validateNonZero then return required error
     * - Given blank value and required when invoke validateNonZero then return required error
     * - Given invalid string when invoke validateNonZero then return success
     */
    @Test
    fun givenValidPositiveNumber_whenInvokeValidatePositive_thenReturnSuccess() {
        val result = Validator.Numbers.validatePositive(5, true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenNullValueAndRequired_whenInvokeValidatePositive_thenReturnRequiredError() {
        val result = Validator.Numbers.validatePositive(null as Int?, true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenNullValueAndNotRequired_whenInvokeValidatePositive_thenReturnSuccess() {
        val result = Validator.Numbers.validatePositive(null as Int?, false)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenZeroValue_whenInvokeValidatePositive_thenReturnPositiveError() {
        val result = Validator.Numbers.validatePositive(0, true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.NUMBER_MUST_BE_POSITIVE))
    }

    @Test
    fun givenNegativeNumber_whenInvokeValidatePositive_thenReturnPositiveError() {
        val result = Validator.Numbers.validatePositive(-5, true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.NUMBER_MUST_BE_POSITIVE))
    }

    @Test
    fun givenValidPositiveNumber_whenInvokeValidatePositiveOrZero_thenReturnSuccess() {
        val result = Validator.Numbers.validatePositiveOrZero(5, true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenZeroValue_whenInvokeValidatePositiveOrZero_thenReturnSuccess() {
        val result = Validator.Numbers.validatePositiveOrZero(0, true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenNegativeNumber_whenInvokeValidatePositiveOrZero_thenReturnPositiveOrZeroError() {
        val result = Validator.Numbers.validatePositiveOrZero(-1, true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.NUMBER_MUST_BE_POSITIVE_OR_ZERO))
    }

    @Test
    fun givenNullValueAndRequired_whenInvokeValidatePositiveOrZero_thenReturnRequiredError() {
        val result = Validator.Numbers.validatePositiveOrZero(null as Int?, true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenValidNonZeroString_whenInvokeValidateNonZero_thenReturnSuccess() {
        val result = Validator.Numbers.validateNonZero("5", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenZeroString_whenInvokeValidateNonZero_thenReturnZeroNotAcceptedError() {
        val result = Validator.Numbers.validateNonZero("0", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.ZERO_IS_NOT_ACCEPTED))
    }

    @Test
    fun givenZeroStringWithSpaces_whenInvokeValidateNonZero_thenReturnZeroNotAcceptedError() {
        val result = Validator.Numbers.validateNonZero("  0  ", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.ZERO_IS_NOT_ACCEPTED))
    }

    @Test
    fun givenNullValueAndRequired_whenInvokeValidateNonZero_thenReturnRequiredError() {
        val result = Validator.Numbers.validateNonZero(null as String?, true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenBlankValueAndRequired_whenInvokeValidateNonZero_thenReturnRequiredError() {
        val result = Validator.Numbers.validateNonZero("   ", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenInvalidString_whenInvokeValidateNonZero_thenReturnSuccess() {
        val result = Validator.Numbers.validateNonZero("abc", true)
        assertTrue(result.isEmpty())
    }

    /**
     * Test cases for [Validator.Strings]
     * - Given valid string in range when invoke validateRange then return success
     * - Given string below minimum when invoke validateRange then return range error
     * - Given string above maximum when invoke validateRange then return range error
     * - Given null value and required when invoke validateRange then return required error
     * - Given String in range inclusive false when invoke validateRange then return success
     * - Given string equal to minimum inclusive when invoke validateRange then return success
     * - Given string equal to minimum exclusive when invoke validateRange then return range error
     * - Given string equal to maximum inclusive when invoke validateRange then return success
     * - Given string equal to maximum exclusive when invoke validateRange then return range error
     * - Given valid string with exact count when invoke validateExactCount then return success
     * - Given string with different count when invoke validateExactCount then return exact count error
     * - Given null value and required when invoke validateExactCount then return required error
     * - Given blank string and required when invoke validateExactCount then return exact count error
     * - Given valid string with exact count not required when invoke validateExactCount then return success
     */

    @Test
    fun givenValidStringInRange_whenInvokeValidateRange_thenReturnSuccess() {
        val result = Validator.Strings.validateRange(value = "test", minCount = 2, maxCount = 10, true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenStringBelowMin_whenInvokeValidateRange_thenReturnRangeError() {
        val result = Validator.Strings.validateRange(value = "a", minCount = 2, maxCount = 10, true)
        assertEquals(1, result.size)
        assertTrue(InputFieldStringCountError.Range(minCount = 2, maxCount = 10) in result)
    }

    @Test
    fun givenStringAboveMax_whenInvokeValidateRange_thenReturnRangeError() {
        val result = Validator.Strings.validateRange(
            value = "this is a very long string", minCount = 2, maxCount = 10, isRequired = true
        )
        assertEquals(1, result.size)
        assertTrue(InputFieldStringCountError.Range(minCount = 2, maxCount = 10) in result)
    }

    @Test
    fun givenNullValueAndRequired_whenInvokeValidateRange_thenReturnRequiredError() {
        val result = Validator.Strings.validateRange(value = null, minCount = 2, maxCount = 10, true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenStringInRangeInclusiveFalse_whenInvokeValidateRange_thenReturnSuccess() {
        val result = Validator.Strings.validateRange(
            value = "abcd", minCount = 3, maxCount = 5, inclusiveMin = false, inclusiveMax = false, isRequired = true
        )
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenStringEqualToMinInclusive_whenInvokeValidateRange_thenReturnSuccess() {
        val result = Validator.Strings.validateRange(
            value = "abc", minCount = 3, maxCount = 5, inclusiveMin = true, isRequired = true
        )
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenStringEqualToMinExclusive_whenInvokeValidateRange_thenReturnRangeError() {
        val result = Validator.Strings.validateRange(
            value = "abc", minCount = 3, maxCount = 5, inclusiveMin = false, isRequired = true
        )
        assertEquals(1, result.size)
        assertTrue(InputFieldStringCountError.Range(minCount = 4, maxCount = 5) in result)

    }

    @Test
    fun givenStringEqualToMaxInclusive_whenInvokeValidateRange_thenReturnSuccess() {
        val result = Validator.Strings.validateRange(
            value = "abcde", minCount = 3, maxCount = 5, inclusiveMax = true, isRequired = true
        )
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenStringEqualToMaxExclusive_whenInvokeValidateRange_thenReturnRangeError() {
        val result = Validator.Strings.validateRange(
            value = "abcde", minCount = 3, maxCount = 5, inclusiveMax = false, isRequired = true
        )
        assertEquals(1, result.size)
        assertTrue(InputFieldStringCountError.Range(minCount = 3, maxCount = 4) in result)
    }

    @Test
    fun givenValidStringWithExactCount_whenInvokeValidateExactCount_thenReturnSuccess() {
        val result = Validator.Strings.validateExactCount(value = "test", count = 4, true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenStringWithDifferentCount_whenInvokeValidateExactCount_thenReturnExactCountError() {
        val result = Validator.Strings.validateExactCount(value = "test", count = 5, true)
        assertEquals(1, result.size)
        assertTrue(InputFieldStringCountError.ExactCount(count = 5) in result)
    }

    @Test
    fun givenNullValueAndRequired_whenInvokeValidateExactCount_thenReturnRequiredError() {
        val result = Validator.Strings.validateExactCount(value = null, count = 4, true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenBlankStringAndRequired_whenInvokeValidateExactCount_thenReturnExactCountError() {
        val result = Validator.Strings.validateExactCount(value = "   ", count = 4, true)
        assertTrue(InputFieldStringCountError.ExactCount(count = 4) in result)
    }

    @Test
    fun givenValidStringWithExactCountNotRequired_whenInvokeValidateExactCount_thenReturnSuccess() {
        val result = Validator.Strings.validateExactCount(value = "test", count = 4, isRequired = false)
        assertTrue(result.isEmpty())
    }


    /**
     * Test cases for [Validator.Email]
     * - Given valid email when invoke validate then return success
     * - Given invalid email when invoke validate then return email invalid error
     * - Given null value and required when invoke validate then return required error
     * - Given blank value and required when invoke validate then return required error
     * - Given invalid email without @ when invoke validate then return email invalid error
     * - Given invalid email without domain when invoke validate then return email invalid error
     */
    @Test
    fun givenValidEmail_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.Email.validate("test@example.com", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenInvalidEmail_whenInvokeValidate_thenReturnEmailInvalidError() {
        val result = Validator.Email.validate("invalid-email", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.EMAIL_IS_NOT_VALID))
    }

    @Test
    fun givenNullValueAndRequired_whenInvokeEmailValidate_thenReturnRequiredError() {
        val result = Validator.Email.validate(null, true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenBlankValueAndRequired_whenInvokeEmailValidate_thenReturnRequiredError() {
        val result = Validator.Email.validate("   ", true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenInvalidEmailWithoutAt_whenInvokeValidate_thenReturnEmailInvalidError() {
        val result = Validator.Email.validate("testexample.com", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.EMAIL_IS_NOT_VALID))
    }

    @Test
    fun givenInvalidEmailWithoutDomain_whenInvokeValidate_thenReturnEmailInvalidError() {
        val result = Validator.Email.validate("test@", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.EMAIL_IS_NOT_VALID))
    }

    /**
     * Test cases for [Validator.Phone] with no country code
     * - Given valid phone when invoke validate then return success
     * - Given the shortest accepted phone when invoke validate then return success
     * - Given the longest accepted phone when invoke validate then return success
     * - Given too short phone when invoke validate then return phone invalid error
     * - Given too long phone when invoke validate then return phone invalid error
     * - Given non-numeric phone when invoke validate then return phone invalid error
     * - Given null value and required when invoke validate then return required error
     */
    @Test
    fun givenValidPhone_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.Phone.validate(phoneNumber = "1234567", isRequired = true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenShortestAcceptedPhone_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.Phone.validate(phoneNumber = "12345678901234567890", isRequired = true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenLongestAcceptedPhone_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.Phone.validate(phoneNumber = "12345678901234567", isRequired = true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenTooShortPhone_whenInvokeValidate_thenReturnPhoneInvalidError() {
        val result = Validator.Phone.validate(phoneNumber = "123", isRequired = true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.PHONE_IS_NOT_VALID))
    }

    @Test
    fun givenTooLongPhone_whenInvokeValidate_thenReturnPhoneInvalidError() {
        val result = Validator.Phone.validate(phoneNumber = "123456789012345678901", isRequired = true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.PHONE_IS_NOT_VALID))
    }

    @Test
    fun givenNonNumericPhone_whenInvokeValidate_thenReturnPhoneInvalidError() {
        val result = Validator.Phone.validate(phoneNumber = "123abc456", isRequired = true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.PHONE_IS_NOT_VALID))
    }

    @Test
    fun givenNullValueAndRequired_whenInvokePhoneValidate_thenReturnRequiredError() {
        val result = Validator.Phone.validate(phoneNumber = null, isRequired = true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    /**
     * Test cases for country-aware [Validator.Phone]
     * - Given a valid number for its country when invoke validate then return success
     * - Given territories sharing a dial code when invoke validate then validate against the named country only
     * - Given a number too short for its country when invoke validate then return invalid digit count
     * - Given a number of a valid length but the wrong shape when invoke validate then return invalid format
     * - Given a number the country-blind rule accepted when invoke validate then reject it
     * - Given an unknown country code when invoke validate then fall back to the country-blind rule
     * - Given a blank country code and it is required when invoke validate then return country code required
     * - Given a blank country code and it is optional when invoke validate then fall back to the country-blind rule
     * - Given a null number and required when invoke validate then return required error
     * - Given a null number and optional when invoke validate then return success
     */
    @Test
    fun givenValidNumberForItsCountry_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.Phone.validate(phoneNumber = "501234567", countryCode = "SA")
        assertTrue(result.isEmpty(), "expected no errors, got $result")
    }

    @Test
    fun givenTerritoriesSharingADialCode_whenInvokeValidate_thenValidateAgainstTheNamedCountryOnly() {
        // The point of taking an ISO code: +1 spans 25 NANP territories, so
        // validating by dial code has to accept a number valid in any of them.
        // A country code names exactly one numbering plan.
        val canadian = Validator.Phone.validate(phoneNumber = "4165550123", countryCode = "CA")
        assertTrue(canadian.isEmpty(), "expected no errors, got $canadian")

        val american = Validator.Phone.validate(phoneNumber = "2015550123", countryCode = "US")
        assertTrue(american.isEmpty(), "expected no errors, got $american")

        // Jersey shares +44 with the UK but has its own plan, and a seven digit
        // UK-only number is not a Jersey number.
        assertTrue(Validator.Phone.validate(phoneNumber = "7400123456", countryCode = "GB").isEmpty())
        assertTrue(Validator.Phone.validate(phoneNumber = "7400123456", countryCode = "JE").isNotEmpty())
    }

    @Test
    fun givenNumberTooShortForItsCountry_whenInvokeValidate_thenReturnInvalidDigitCount() {
        val result = Validator.Phone.validate(phoneNumber = "12345", countryCode = "SA")
        assertEquals(1, result.size)
        val error = result.assertSingleIs<InputFieldPhoneError.InvalidDigitCount>()
        assertEquals(listOf(9), error.expectedLengths)
        assertEquals(5, error.currentCount)
        assertTrue(error.isContiguous)
    }

    @Test
    fun givenNumberOfValidLengthButWrongShape_whenInvokeValidate_thenReturnInvalidFormat() {
        val result = Validator.Phone.validate(phoneNumber = "301234567", countryCode = "SA")
        val error = result.assertSingleIs<InputFieldPhoneError.InvalidFormat>()
        assertEquals("SA", error.countryCode)
        assertEquals("512345678", error.example)
    }

    @Test
    fun givenCountryWithGappedLengths_whenInvokeValidate_thenReportThemAsNonContiguous() {
        val result = Validator.Phone.validate(phoneNumber = "123456", countryCode = "IS")
        val error = result.assertSingleIs<InputFieldPhoneError.InvalidDigitCount>()
        assertEquals(listOf(7, 9), error.expectedLengths)
        assertTrue(error.isContiguous.not())
    }

    @Test
    fun givenNumberTheCountryBlindRuleAccepted_whenInvokeValidate_thenRejectIt() {
        val nonsense = "12345678901234"
        assertTrue(Validator.Phone.validate(phoneNumber = nonsense).isEmpty())
        assertTrue(Validator.Phone.validate(phoneNumber = nonsense, countryCode = "SA").isNotEmpty())
    }

    @Test
    fun givenUnknownCountryCode_whenInvokePhoneValidate_thenFallBackToTheCountryBlindRule() {
        val valid = Validator.Phone.validate(phoneNumber = "1234567", countryCode = "ZZ")
        assertTrue(valid.isEmpty(), "expected the country-blind rule to accept, got $valid")

        val invalid = Validator.Phone.validate(phoneNumber = "123", countryCode = "ZZ")
        assertEquals(setOf<IInputFieldError>(InputFieldError.PHONE_IS_NOT_VALID), invalid)
    }

    @Test
    fun givenBlankCountryCodeAndItIsRequired_whenInvokeValidate_thenReturnCountryCodeRequired() {
        val result = Validator.Phone.validate(
            phoneNumber = "1234567", countryCode = null, isCountryCodeRequired = true
        )
        assertTrue(result.contains(InputFieldPhoneError.CountryCodeRequired), "got $result")
    }

    @Test
    fun givenBlankCountryCodeAndItIsOptional_whenInvokeValidate_thenFallBackToTheCountryBlindRule() {
        val result = Validator.Phone.validate(
            phoneNumber = "1234567", countryCode = "", isCountryCodeRequired = false
        )
        assertTrue(result.isEmpty(), "expected no errors, got $result")
    }

    @Test
    fun givenNullNumberAndRequired_whenInvokeValidate_thenReturnRequiredError() {
        val result = Validator.Phone.validate(phoneNumber = null, countryCode = "SA")
        assertEquals(setOf<IInputFieldError>(InputFieldError.REQUIRED), result)
    }

    @Test
    fun givenNullNumberAndOptional_whenInvokeValidate_thenReturnSuccess() {
        val nullNumber = Validator.Phone.validate(
            phoneNumber = null, countryCode = "SA", isRequired = false
        )
        assertTrue(nullNumber.isEmpty())

        val blankNumber = Validator.Phone.validate(
            phoneNumber = "   ", countryCode = "SA", isRequired = false
        )
        assertTrue(blankNumber.isEmpty())
    }

    @Test
    fun givenNumberWithTrunkPrefixOrDialCode_whenInvokeValidate_thenReturnSuccess() {
        listOf("0501234567", "966501234567", "+966 50 123 4567", "٥٠١٢٣٤٥٦٧").forEach { number ->
            val result = Validator.Phone.validate(phoneNumber = number, countryCode = "SA")
            assertTrue(result.isEmpty(), "expected $number to be accepted, got $result")
        }
    }

    /**
     * Test cases for [Validator.Password]
     * - Given valid password when invoke validate then return success
     * - Given short password when invoke validate then return length range error
     * - Given password without uppercase when invoke validate then return uppercase error
     * - Given password without lowercase when invoke validate then return lowercase error
     * - Given password without numbers when invoke validate then return number error
     * - Given password without special char when invoke validate then return special char error
     * - Given password with multiple errors when invoke validate then return multiple errors
     * - Given null password and required when invoke validate then return required error
     * - Given matching passwords when invoke validateConfirmation then return success
     * - Given non-matching passwords when invoke validateConfirmation then return not same error
     * - Given null confirmation and required when invoke validateConfirmation then return required error
     */
    @Test
    fun givenValidPassword_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.Password.validate("TestPass123!", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenShortPassword_whenInvokeValidate_thenReturnLengthRangeError() {
        val result = Validator.Password.validate("Test1!", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.PASSWORD_LENGTH_RANGE))
    }

    @Test
    fun givenPasswordWithoutUppercase_whenInvokeValidate_thenReturnUppercaseError() {
        val result = Validator.Password.validate("testpass123!", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.PASSWORD_UPPERCASE))
    }

    @Test
    fun givenPasswordWithoutLowercase_whenInvokeValidate_thenReturnLowercaseError() {
        val result = Validator.Password.validate("TESTPASS123!", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.PASSWORD_LOWERCASE))
    }

    @Test
    fun givenPasswordWithoutNumbers_whenInvokeValidate_thenReturnNumberError() {
        val result = Validator.Password.validate("TestPassword!", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.PASSWORD_NUMBER))
    }

    @Test
    fun givenPasswordWithoutSpecialChar_whenInvokeValidate_thenReturnSpecialCharError() {
        val result = Validator.Password.validate("TestPass123", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.PASSWORD_SPECIAL_CHAR))
    }

    @Test
    fun givenPasswordWithMultipleErrors_whenInvokeValidate_thenReturnMultipleErrors() {
        val result = Validator.Password.validate("test", true)
        assertTrue(result.size >= 4)
        assertTrue(result.contains(InputFieldError.PASSWORD_LENGTH_RANGE))
        assertTrue(result.contains(InputFieldError.PASSWORD_UPPERCASE))
        assertTrue(result.contains(InputFieldError.PASSWORD_NUMBER))
        assertTrue(result.contains(InputFieldError.PASSWORD_SPECIAL_CHAR))
    }

    @Test
    fun givenNullPasswordAndRequired_whenInvokeValidate_thenReturnRequiredError() {
        val result = Validator.Password.validate(null, true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenMatchingPasswords_whenInvokeValidateConfirmation_thenReturnSuccess() {
        val result = Validator.Password.validateConfirmation("password", "password", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenNonMatchingPasswords_whenInvokeValidateConfirmation_thenReturnNotSameError() {
        val result = Validator.Password.validateConfirmation("password1", "password2", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.REPEATED_PASSWORD_ARE_NOT_THE_SAME))
    }

    @Test
    fun givenNullConfirmationAndRequired_whenInvokeValidateConfirmation_thenReturnRequiredError() {
        val result = Validator.Password.validateConfirmation("password", null, true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    /**
     * Test cases for [Validator.URL]
     * - Given valid HTTPS URL when invoke validate then return success
     * - Given valid HTTP URL when invoke validate then return success
     * - Given URL with white spaces when invoke validate then return website url contains spaces error
     * - Given URL without protocol when invoke validate then return protocol required error
     * - Given URL with invalid domain when invoke validate then return domain invalid error
     * - Given null URL and required when invoke validate then return required error
     */
    @Test
    fun givenValidHttpsUrl_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.URL.validate("https://www.example.com", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenValidHttpUrl_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.URL.validate("http://www.example.com", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenUrlWithWhiteSpaces_whenInvokeValidate_thenReturnWebsiteUrlContainsSpaces() {
        val result = Validator.URL.validate("http:// www.example.com", true)
        assertEquals(2, result.size)
        assertTrue(result.contains(InputFieldError.WEBSITE_URL_CONTAINS_SPACES))
    }

    @Test
    fun givenUrlWithoutProtocol_whenInvokeValidate_thenReturnProtocolRequiredError() {
        val result = Validator.URL.validate("www.example.com", true)
        assertEquals(2, result.size)
        assertTrue(result.contains(InputFieldError.WEBSITE_URL_PROTOCOL_IS_REQUIRED))
    }

    @Test
    fun givenUrlWithInvalidDomain_whenInvokeValidate_thenReturnDomainInvalidError() {
        val result = Validator.URL.validate("https://invalid", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.WEBSITE_DOMAIN_IS_NOT_VALID))
    }

    @Test
    fun givenNullUrlAndRequired_whenInvokeValidate_thenReturnRequiredError() {
        val result = Validator.URL.validate(null, true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    /**
     * Test cases for [Validator.PostalCode] with no country code
     * - Given valid postal code when invoke validate then return success
     * - Given valid long postal code when invoke validate then return success
     * - Given short postal code when invoke validate then return postal code invalid error
     * - Given too long postal code when invoke validate then return postal code invalid error
     * - Given non-numeric postal code when invoke validate then return postal code invalid error
     * - Given null postal code and required when invoke validate then return required error
     */
    @Test
    fun givenValidPostalCode_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.PostalCode.validate(postalCode = "123", isRequired = true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenValidLongPostalCode_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.PostalCode.validate(postalCode = "1234567890", isRequired = true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenShortPostalCode_whenInvokeValidate_thenReturnPostalCodeInvalidError() {
        val result = Validator.PostalCode.validate(postalCode = "12", isRequired = true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.POSTAL_CODE_IS_NOT_VALID))
    }

    @Test
    fun givenTooLongPostalCode_whenInvokeValidate_thenReturnPostalCodeInvalidError() {
        val result = Validator.PostalCode.validate(postalCode = "12345678901", isRequired = true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.POSTAL_CODE_IS_NOT_VALID))
    }

    @Test
    fun givenNonNumericPostalCode_whenInvokeValidate_thenReturnPostalCodeInvalidError() {
        val result = Validator.PostalCode.validate(postalCode = "12a45", isRequired = true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.POSTAL_CODE_IS_NOT_VALID))
    }

    @Test
    fun givenNullPostalCodeAndRequired_whenInvokeValidate_thenReturnRequiredError() {
        val result = Validator.PostalCode.validate(postalCode = null, isRequired = true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    /**
     * Test cases for country-aware [Validator.PostalCode]
     * - Given an alphanumeric postal code for its country when invoke validate then return success
     * - Given a postal code the country-blind rule rejected when invoke validate then accept it
     * - Given a country with several formats when invoke validate then accept each
     * - Given an invalid postal code for its country when invoke validate then return invalid format
     * - Given a country with no postal system when invoke validate then skip validation
     * - Given a blank postal code and required in a country with no postal system when invoke validate then return required error
     * - Given a country absent from the reference data when invoke validate then fall back to the country-blind rule
     * - Given an unknown ISO code when invoke validate then fall back to the country-blind rule
     * - Given a null postal code and required for its country when invoke validate then return required error
     * - Given a null postal code and optional when invoke validate then return success
     */
    @Test
    fun givenAlphanumericPostalCodeForItsCountry_whenInvokeValidate_thenReturnSuccess() {
        mapOf("GB" to "SW1A 1AA", "CA" to "K1A 0B1", "NL" to "1234 AB", "SA" to "11564")
            .forEach { (countryCode, postalCode) ->
                val result = Validator.PostalCode.validate(postalCode = postalCode, countryCode = countryCode)
                assertTrue(result.isEmpty(), "expected $countryCode $postalCode to be accepted, got $result")
            }
    }

    @Test
    fun givenPostalCodeTheCountryBlindRuleRejected_whenInvokeValidate_thenAcceptIt() {
        val postalCode = "SW1A 1AA"
        assertTrue(Validator.PostalCode.validate(postalCode = postalCode).isNotEmpty())
        assertTrue(Validator.PostalCode.validate(postalCode = postalCode, countryCode = "GB").isEmpty())
    }

    @Test
    fun givenCountryWithSeveralFormats_whenInvokeValidate_thenAcceptEach() {
        assertTrue(Validator.PostalCode.validate(postalCode = "4460232", countryCode = "EG").isEmpty())
        assertTrue(Validator.PostalCode.validate(postalCode = "12345", countryCode = "EG").isEmpty())
    }

    @Test
    fun givenInvalidPostalCodeForItsCountry_whenInvokeValidate_thenReturnInvalidFormat() {
        val result = Validator.PostalCode.validate(postalCode = "1234", countryCode = "SA")
        val error = result.assertSingleIs<InputFieldPostalCodeError.InvalidFormat>()
        assertEquals("SA", error.countryCode)
        assertEquals("11564", error.example)
    }

    @Test
    fun givenCountryWithNoPostalSystem_whenInvokeValidate_thenSkipValidation() {
        listOf("12345", "SW1A 1AA", "nonsense").forEach { postalCode ->
            val result = Validator.PostalCode.validate(postalCode = postalCode, countryCode = "AE")
            assertTrue(result.isEmpty(), "expected $postalCode to be skipped, got $result")
        }
        assertTrue(
            Validator.PostalCode
                .validate(postalCode = "12345", countryCode = "AE", isRequired = false)
                .isEmpty()
        )
    }

    @Test
    fun givenBlankPostalCodeAndRequiredInCountryWithNoPostalSystem_whenInvokeValidate_thenReturnRequiredError() {
        // The value is checked before the country is read, so a required field
        // still reports a missing value even where no postal code exists to give.
        listOf(null, "", "   ").forEach { postalCode ->
            val result = Validator.PostalCode.validate(postalCode = postalCode, countryCode = "AE")
            assertEquals(setOf<IInputFieldError>(InputFieldError.REQUIRED), result)
        }
        assertTrue(
            Validator.PostalCode
                .validate(postalCode = null, countryCode = "AE", isRequired = false)
                .isEmpty()
        )
    }

    @Test
    fun givenCountryWithNoPostalSystem_whenReadItsRule_thenReportItHasNone() {
        val rule = requireNotNull(CountryRules.forCountryCode("AE"))
        assertEquals(false, rule.hasPostalSystem)
        assertNull(rule.postalPattern)
    }

    @Test
    fun givenCountryAbsentFromReferenceData_whenInvokeValidate_thenFallBackToTheCountryBlindRule() {
        val valid = Validator.PostalCode.validate(postalCode = "12345", countryCode = "SY")
        assertTrue(valid.isEmpty(), "expected the country-blind rule to accept, got $valid")

        val invalid = Validator.PostalCode.validate(postalCode = "AB", countryCode = "SY")
        assertEquals(setOf<IInputFieldError>(InputFieldError.POSTAL_CODE_IS_NOT_VALID), invalid)
    }

    @Test
    fun givenUnknownCountryCode_whenInvokePostalValidate_thenFallBackToTheCountryBlindRule() {
        val valid = Validator.PostalCode.validate(postalCode = "12345", countryCode = "ZZ")
        assertTrue(valid.isEmpty(), "expected the country-blind rule to accept, got $valid")

        val invalid = Validator.PostalCode.validate(postalCode = "SW1A 1AA", countryCode = "ZZ")
        assertEquals(setOf<IInputFieldError>(InputFieldError.POSTAL_CODE_IS_NOT_VALID), invalid)
    }

    @Test
    fun givenNullPostalCodeAndRequiredForItsCountry_whenInvokeValidate_thenReturnRequiredError() {
        val result = Validator.PostalCode.validate(postalCode = null, countryCode = "GB")
        assertEquals(setOf<IInputFieldError>(InputFieldError.REQUIRED), result)
    }

    @Test
    fun givenNullPostalCodeAndOptional_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.PostalCode.validate(
            postalCode = null, countryCode = "GB", isRequired = false
        )
        assertTrue(result.isEmpty())
    }

    /**
     * Test cases for [Validator.Version]
     * - Given valid version when invoke validate then return success
     * - Given two digit parts version when invoke validate then return success
     * - Given two segments version when invoke validate then return version invalid error
     * - Given three digit part version when invoke validate then return version invalid error
     * - Given non numeric version when invoke validate then return version invalid error
     * - Given null version and required when invoke validate then return required error
     */
    @Test
    fun givenValidVersion_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.Version.validate("1.2.3", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenTwoDigitPartsVersion_whenInvokeValidate_thenReturnSuccess() {
        val result = Validator.Version.validate("10.20.30", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenTwoSegmentsVersion_whenInvokeValidate_thenReturnVersionInvalidError() {
        val result = Validator.Version.validate("1.2", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.VERSION_IS_NOT_VALID))
    }

    @Test
    fun givenThreeDigitPartVersion_whenInvokeValidate_thenReturnVersionInvalidError() {
        val result = Validator.Version.validate("1.2.345", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.VERSION_IS_NOT_VALID))
    }

    @Test
    fun givenNonNumericVersion_whenInvokeValidate_thenReturnVersionInvalidError() {
        val result = Validator.Version.validate("1.2.3-beta", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.VERSION_IS_NOT_VALID))
    }

    @Test
    fun givenNullVersionAndRequired_whenInvokeValidate_thenReturnRequiredError() {
        val result = Validator.Version.validate(null, true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    /**
     * Test cases for [Validator.Location]
     * - Given valid latitude when invoke validateLatitude then return success
     * - Given valid negative latitude when invoke validateLatitude then return success
     * - Given valid boundary latitude when invoke validateLatitude then return success
     * - Given invalid latitude when invoke validateLatitude then return latitude invalid error
     * - Given null latitude and required when invoke validateLatitude then return required error
     * - Given valid longitude when invoke validateLongitude then return success
     * - Given valid negative longitude when invoke validateLongitude then return success
     * - Given valid boundary longitude when invoke validateLongitude then return success
     * - Given invalid longitude when invoke validateLongitude then return longitude invalid error
     * - Given null longitude and required when invoke validateLongitude then return required error
     */
    @Test
    fun givenValidLatitude_whenInvokeValidateLatitude_thenReturnSuccess() {
        val result = Validator.Location.validateLatitude("45.5", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenValidNegativeLatitude_whenInvokeValidateLatitude_thenReturnSuccess() {
        val result = Validator.Location.validateLatitude("-45.5", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenValidBoundaryLatitude_whenInvokeValidateLatitude_thenReturnSuccess() {
        val result = Validator.Location.validateLatitude("90", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenInvalidLatitude_whenInvokeValidateLatitude_thenReturnLatitudeInvalidError() {
        val result = Validator.Location.validateLatitude("91", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.LATITUDE_IS_NOT_VALID))
    }

    @Test
    fun givenNullLatitudeAndRequired_whenInvokeValidateLatitude_thenReturnRequiredError() {
        val result = Validator.Location.validateLatitude(null, true)
        println(result)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenValidLongitude_whenInvokeValidateLongitude_thenReturnSuccess() {
        val result = Validator.Location.validateLongitude("123.5", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenValidNegativeLongitude_whenInvokeValidateLongitude_thenReturnSuccess() {
        val result = Validator.Location.validateLongitude("-123.5", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenValidBoundaryLongitude_whenInvokeValidateLongitude_thenReturnSuccess() {
        val result = Validator.Location.validateLongitude("180", true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenInvalidLongitude_whenInvokeValidateLongitude_thenReturnLongitudeInvalidError() {
        val result = Validator.Location.validateLongitude("181", true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.LONGITUDE_IS_NOT_VALID))
    }

    @Test
    fun givenNullLongitudeAndRequired_whenInvokeValidateLongitude_thenReturnRequiredError() {
        val result = Validator.Location.validateLongitude(null, true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    /**
     * Test cases for [Validator.Date]
     * - Given past date when invoke validateNotAfterToday then return success
     * - Given future date when invoke validateNotAfterToday then return future error
     * - Given null date when invoke validateNotAfterToday then return required error
     * - Given future date when invoke validateNotBeforeToday then return success
     * - Given past date when invoke validateNotBeforeToday then return past error
     * - Given null date when invoke validateNotBeforeToday then return required error
     * - Given valid date range when invoke validateRange then return success
     * - Given invalid date range when invoke validateRange then return range error
     * - Given same dates in range when invoke validateRange then return success
     */
    @Test
    fun givenPastDate_whenInvokeValidateNotAfterToday_thenReturnSuccess() {
        val pastDate = LocalDate(2000, 1, 1)
        val result = Validator.Date.validateNotAfterToday(pastDate, true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenFutureDate_whenInvokeValidateNotAfterToday_thenReturnFutureError() {
        val futureDate = LocalDate(2100, 1, 1)
        val result = Validator.Date.validateNotAfterToday(futureDate, true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.DATE_CANNOT_BE_FUTURE))
    }

    @Test
    fun givenNullDate_whenInvokeValidateNotAfterToday_thenReturnRequiredError() {
        val result = Validator.Date.validateNotAfterToday(null, true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenFutureDate_whenInvokeValidateNotBeforeToday_thenReturnSuccess() {
        val futureDate = LocalDate(2100, 1, 1)
        val result = Validator.Date.validateNotBeforeToday(futureDate, true)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenPastDate_whenInvokeValidateNotBeforeToday_thenReturnPastError() {
        val pastDate = LocalDate(2000, 1, 1)
        val result = Validator.Date.validateNotBeforeToday(pastDate, true)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.DATE_CANNOT_BE_IN_PAST))
    }

    @Test
    fun givenNullDate_whenInvokeValidateNotBeforeToday_thenReturnRequiredError() {
        val result = Validator.Date.validateNotBeforeToday(null, true)
        assertTrue(result.contains(InputFieldError.REQUIRED))
    }

    @Test
    fun givenValidDateRange_whenInvokeValidateRange_thenReturnSuccess() {
        val from = LocalDate(2000, 1, 1)
        val to = LocalDate(2000, 12, 31)
        val result = Validator.Date.validateRange(from, to)
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenInvalidDateRange_whenInvokeValidateRange_thenReturnRangeError() {
        val from = LocalDate(2000, 12, 31)
        val to = LocalDate(2000, 1, 1)
        val result = Validator.Date.validateRange(from, to)
        assertEquals(1, result.size)
        assertTrue(result.contains(InputFieldError.INVALID_DATE_RANGE))
    }

    @Test
    fun givenSameDatesInRange_whenInvokeValidateRange_thenReturnSuccess() {
        val date = LocalDate(2000, 1, 1)
        val result = Validator.Date.validateRange(date, date)
        assertTrue(result.isEmpty())
    }

    private inline fun <reified Error> Set<IInputFieldError>.assertSingleIs(): Error {
        assertEquals(1, size, "expected exactly one error, got $this")
        val error = first()
        assertIs<Error>(error)
        return error
    }
}