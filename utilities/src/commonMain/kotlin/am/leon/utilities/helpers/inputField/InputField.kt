package am.leon.utilities.helpers.inputField

import am.leon.utilities.extentions.toPlainString

interface IInputFieldKey {
    val title: String
}

interface IInputFieldError {
    val title: String
}

sealed class InputFieldStringCountError(
    override val title: String = "input_field_string_count_error"
) : IInputFieldError {
    data class Range(val minCount: Int? = null, val maxCount: Int? = null) :
        InputFieldStringCountError()

    data class ExactCount(val count: Int) : InputFieldStringCountError()
}

sealed interface InputFieldNumberRangeError : IInputFieldError {
    override val title: String get() = "input_field_number_range_error"

    data class Range<Value : Number>(
        val minValue: Value? = null,
        val maxValue: Value? = null
    ) : InputFieldNumberRangeError {
        val minValueString: String? get() = minValue?.toPlainString(isScaled = false, decimals = 0)
        val maxValueString: String? get() = maxValue?.toPlainString(isScaled = false, decimals = 0)
    }

    data class ExactValue<Value : Number>(val value: Value?) : InputFieldNumberRangeError {
        val valueString: String? get() = value?.toPlainString(isScaled = false, decimals = 0)
    }
}

sealed interface InputFieldPhoneError : IInputFieldError {
    override val title: String get() = "input_field_phone_error"

    data class InvalidDigitCount(
        val expectedLengths: List<Int>,
        val currentCount: Int,
    ) : InputFieldPhoneError {
        val minCount: Int? get() = expectedLengths.minOrNull()
        val maxCount: Int? get() = expectedLengths.maxOrNull()

        val isContiguous: Boolean
            get() {
                val min = minCount ?: return false
                val max = maxCount ?: return false
                return expectedLengths.size == max - min + 1
            }
    }

    data class InvalidFormat(val countryCode: String, val example: String?) : InputFieldPhoneError

    data object CountryCodeRequired : InputFieldPhoneError
}

sealed interface InputFieldPostalCodeError : IInputFieldError {
    override val title: String get() = "input_field_postal_code_error"

    data class InvalidFormat(val countryCode: String, val example: String?) :
        InputFieldPostalCodeError
}

enum class InputFieldError(override val title: String) : IInputFieldError {
    // Numbers
    NUMBER_MUST_BE_POSITIVE("number_must_be_positive"),
    NUMBER_MUST_BE_POSITIVE_OR_ZERO("number_must_be_positive_or_zero"),
    ZERO_IS_NOT_ACCEPTED("zero_is_not_accepted"),

    // Email
    EMAIL_IS_NOT_VALID("email_is_not_valid"),

    // Phone
    PHONE_IS_NOT_VALID("phone_is_not_valid"),

    // Password
    PASSWORD_IS_NOT_VALID("password_is_not_valid"),
    PASSWORD_LENGTH_RANGE("password_length_range"),
    PASSWORD_UPPERCASE("password_uppercase"),
    PASSWORD_LOWERCASE("password_lowercase"),
    PASSWORD_NUMBER("password_number"),
    PASSWORD_SPECIAL_CHAR("password_special_char"),
    REPEATED_PASSWORD_ARE_NOT_THE_SAME("repeated_password_are_not_the_same"),

    // URL
    WEBSITE_DOMAIN_IS_NOT_VALID("website_domain_is_not_valid"),
    WEBSITE_URL_PROTOCOL_IS_REQUIRED("website_url_protocol_is_required"),
    WEBSITE_URL_CONTAINS_SPACES("website_url_contains_spaces"),

    // Postal Code
    POSTAL_CODE_IS_NOT_VALID("postal_code_is_not_valid"),

    // Version
    VERSION_IS_NOT_VALID("version_is_not_valid"),

    // Location
    LATITUDE_IS_NOT_VALID("latitude_is_not_valid"),
    LONGITUDE_IS_NOT_VALID("longitude_is_not_valid"),

    // Date
    DATE_CANNOT_BE_FUTURE("date_cannot_be_future"),
    DATE_CANNOT_BE_IN_PAST("date_cannot_be_in_past"),
    INVALID_DATE_RANGE("invalid_date_range"),

    EMPTY("empty"),
    REQUIRED("required"),
    INITIAL_STATE("initial_state"),
    REQUEST_KEY_MISSING_FROM_REQUEST("request_key_missing_from_request"),
    OTHER("");

    companion object {
        fun find(key: String?): InputFieldError = when {
            key.isNullOrEmpty() -> OTHER
            else -> entries.firstOrNull { it.title == key } ?: OTHER
        }
    }
}