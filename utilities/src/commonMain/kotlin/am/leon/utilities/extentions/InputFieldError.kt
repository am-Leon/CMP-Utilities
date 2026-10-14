package am.leon.utilities.extentions

import am.leon.utilities.generated.resources.Res
import am.leon.utilities.generated.resources.at_least_8_characters_and_less_than_50
import am.leon.utilities.generated.resources.at_least_one_lowercase
import am.leon.utilities.generated.resources.at_least_one_number
import am.leon.utilities.generated.resources.at_least_one_special_char
import am.leon.utilities.generated.resources.at_least_one_uppercase
import am.leon.utilities.generated.resources.character_range_exact
import am.leon.utilities.generated.resources.character_range_max_only
import am.leon.utilities.generated.resources.character_range_min_max
import am.leon.utilities.generated.resources.character_range_min_only
import am.leon.utilities.generated.resources.date_cannot_be_future
import am.leon.utilities.generated.resources.date_cannot_be_in_past
import am.leon.utilities.generated.resources.email_not_valid
import am.leon.utilities.generated.resources.field_should_not_be_empty
import am.leon.utilities.generated.resources.invalid_character_count
import am.leon.utilities.generated.resources.invalid_date_range
import am.leon.utilities.generated.resources.latitude_is_not_valid
import am.leon.utilities.generated.resources.longitude_is_not_valid
import am.leon.utilities.generated.resources.number_must_be_positive
import am.leon.utilities.generated.resources.number_must_be_positive_or_zero
import am.leon.utilities.generated.resources.number_range_exact
import am.leon.utilities.generated.resources.number_range_invalid
import am.leon.utilities.generated.resources.number_range_max_only
import am.leon.utilities.generated.resources.number_range_min_max
import am.leon.utilities.generated.resources.number_range_min_only
import am.leon.utilities.generated.resources.password_is_not_valid
import am.leon.utilities.generated.resources.phone_country_code_required
import am.leon.utilities.generated.resources.phone_digit_count_exact
import am.leon.utilities.generated.resources.phone_digit_count_range
import am.leon.utilities.generated.resources.phone_not_valid
import am.leon.utilities.generated.resources.phone_not_valid_with_example
import am.leon.utilities.generated.resources.postal_code_invalid
import am.leon.utilities.generated.resources.postal_code_invalid_with_example
import am.leon.utilities.generated.resources.repeated_password_are_not_the_same
import am.leon.utilities.generated.resources.request_key_missing
import am.leon.utilities.generated.resources.required
import am.leon.utilities.generated.resources.should_not_zero
import am.leon.utilities.generated.resources.unknown_error_occur
import am.leon.utilities.generated.resources.version_is_not_valid
import am.leon.utilities.generated.resources.website_domain_is_not_valid
import am.leon.utilities.generated.resources.website_protocol_is_not_valid
import am.leon.utilities.generated.resources.website_url_contains_spaces
import am.leon.utilities.helpers.inputField.IInputFieldError
import am.leon.utilities.helpers.inputField.InputFieldError
import am.leon.utilities.helpers.inputField.InputFieldNumberRangeError
import am.leon.utilities.helpers.inputField.InputFieldPhoneError
import am.leon.utilities.helpers.inputField.InputFieldPostalCodeError
import am.leon.utilities.helpers.inputField.InputFieldStringCountError
import org.jetbrains.compose.resources.StringResource

fun Set<IInputFieldError>.mapToCombinedString(block: (IInputFieldError) -> IString? = { null }): IString.Combined {
    val errorMessages = map { fieldError ->
        when (fieldError) {
            is InputFieldError -> fieldError.getErrorMessage()
            is InputFieldStringCountError.Range -> fieldError.getErrorMessage()
            is InputFieldStringCountError.ExactCount -> fieldError.getErrorMessage()
            is InputFieldNumberRangeError.Range<*> -> fieldError.getErrorMessage()
            is InputFieldNumberRangeError.ExactValue<*> -> fieldError.getErrorMessage()
            is InputFieldPhoneError -> fieldError.getErrorMessage()
            is InputFieldPostalCodeError -> fieldError.getErrorMessage()
            else -> block.invoke(fieldError) ?: IString.MultiplatformRes(Res.string.unknown_error_occur)
        }
    }.toSet()

    return IString.Combined(errorMessages)
}

fun InputFieldError.getErrorMessage(): IString {
    val errorResource = InputFieldErrorMessage.getOrElse(this) { Res.string.unknown_error_occur }
    return IString.MultiplatformRes(errorResource)
}

fun InputFieldStringCountError.Range.getErrorMessage(): IString {
    return when {
        minCount != null && maxCount != null && minCount != maxCount ->
            IString.MultiplatformRes(Res.string.character_range_min_max, minCount, maxCount)

        minCount != null -> IString.MultiplatformRes(
            Res.string.character_range_min_only, minCount
        )

        maxCount != null -> IString.MultiplatformRes(
            Res.string.character_range_max_only, maxCount
        )

        else -> IString.MultiplatformRes(Res.string.invalid_character_count)
    }
}

fun InputFieldStringCountError.ExactCount.getErrorMessage(): IString {
    return IString.MultiplatformRes(Res.string.character_range_exact, count)
}

fun InputFieldNumberRangeError.Range<*>.getErrorMessage(): IString {
    return when {
        minValueString != null && maxValueString != null && minValueString != maxValueString -> IString.MultiplatformRes(
            Res.string.number_range_min_max, minValueString.orEmpty(), maxValueString.orEmpty()
        )

        minValueString != null -> IString.MultiplatformRes(
            Res.string.number_range_min_only, minValueString.orEmpty()
        )

        maxValueString != null ->
            IString.MultiplatformRes(
                Res.string.number_range_max_only,
                maxValueString.orEmpty()
            )

        else -> IString.MultiplatformRes(Res.string.number_range_invalid)
    }
}

fun InputFieldNumberRangeError.ExactValue<*>.getErrorMessage(): IString {
    return IString.MultiplatformRes(Res.string.number_range_exact, valueString.orEmpty())
}

fun InputFieldPhoneError.getErrorMessage(): IString = when (this) {
    is InputFieldPhoneError.InvalidDigitCount -> digitCountErrorMessage()

    is InputFieldPhoneError.InvalidFormat -> when {
        example != null -> IString.MultiplatformRes(Res.string.phone_not_valid_with_example, example)
        else -> IString.MultiplatformRes(Res.string.phone_not_valid)
    }

    InputFieldPhoneError.CountryCodeRequired ->
        IString.MultiplatformRes(Res.string.phone_country_code_required)
}

private fun InputFieldPhoneError.InvalidDigitCount.digitCountErrorMessage(): IString {
    val min = minCount
    val max = maxCount
    return when {
        min == null || max == null -> IString.MultiplatformRes(Res.string.phone_not_valid)

        min == max -> IString.MultiplatformRes(Res.string.phone_digit_count_exact, min)

        isContiguous -> IString.MultiplatformRes(Res.string.phone_digit_count_range, min, max)

        else -> IString.MultiplatformRes(
            Res.string.phone_digit_count_exact, expectedLengths.joinToString(separator = ", ")
        )
    }
}

fun InputFieldPostalCodeError.getErrorMessage(): IString = when (this) {
    is InputFieldPostalCodeError.InvalidFormat -> when {
        example != null ->
            IString.MultiplatformRes(Res.string.postal_code_invalid_with_example, example)

        else -> IString.MultiplatformRes(Res.string.postal_code_invalid)
    }
}

private val InputFieldErrorMessage: Map<InputFieldError, StringResource> = mapOf(
    // Fields
    InputFieldError.EMAIL_IS_NOT_VALID to Res.string.email_not_valid,
    InputFieldError.PHONE_IS_NOT_VALID to Res.string.phone_not_valid,
    InputFieldError.POSTAL_CODE_IS_NOT_VALID to Res.string.postal_code_invalid,
    InputFieldError.VERSION_IS_NOT_VALID to Res.string.version_is_not_valid,

    // Password
    InputFieldError.PASSWORD_IS_NOT_VALID to Res.string.password_is_not_valid,
    InputFieldError.PASSWORD_LENGTH_RANGE to Res.string.at_least_8_characters_and_less_than_50,
    InputFieldError.PASSWORD_UPPERCASE to Res.string.at_least_one_uppercase,
    InputFieldError.PASSWORD_LOWERCASE to Res.string.at_least_one_lowercase,
    InputFieldError.PASSWORD_NUMBER to Res.string.at_least_one_number,
    InputFieldError.PASSWORD_SPECIAL_CHAR to Res.string.at_least_one_special_char,
    InputFieldError.REPEATED_PASSWORD_ARE_NOT_THE_SAME to Res.string.repeated_password_are_not_the_same,

    // URL
    InputFieldError.WEBSITE_DOMAIN_IS_NOT_VALID to Res.string.website_domain_is_not_valid,
    InputFieldError.WEBSITE_URL_PROTOCOL_IS_REQUIRED to Res.string.website_protocol_is_not_valid,
    InputFieldError.WEBSITE_URL_CONTAINS_SPACES to Res.string.website_url_contains_spaces,

    // Numbers
    InputFieldError.NUMBER_MUST_BE_POSITIVE to Res.string.number_must_be_positive,
    InputFieldError.NUMBER_MUST_BE_POSITIVE_OR_ZERO to Res.string.number_must_be_positive_or_zero,
    InputFieldError.ZERO_IS_NOT_ACCEPTED to Res.string.should_not_zero,

    // Location
    InputFieldError.LATITUDE_IS_NOT_VALID to Res.string.latitude_is_not_valid,
    InputFieldError.LONGITUDE_IS_NOT_VALID to Res.string.longitude_is_not_valid,

    // General
    InputFieldError.REQUIRED to Res.string.required,
    InputFieldError.INITIAL_STATE to Res.string.field_should_not_be_empty,
    InputFieldError.EMPTY to Res.string.field_should_not_be_empty,
    InputFieldError.REQUEST_KEY_MISSING_FROM_REQUEST to Res.string.request_key_missing,
    InputFieldError.OTHER to Res.string.unknown_error_occur,

    // Date
    InputFieldError.DATE_CANNOT_BE_FUTURE to Res.string.date_cannot_be_future,
    InputFieldError.DATE_CANNOT_BE_IN_PAST to Res.string.date_cannot_be_in_past,
    InputFieldError.INVALID_DATE_RANGE to Res.string.invalid_date_range,
)