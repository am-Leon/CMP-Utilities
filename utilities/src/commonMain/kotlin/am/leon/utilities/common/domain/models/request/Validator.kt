package am.leon.utilities.common.domain.models.request

import am.leon.utilities.extentions.now
import am.leon.utilities.helpers.country.CountryRules
import am.leon.utilities.helpers.country.CountryValidationRule
import am.leon.utilities.helpers.inputField.IInputFieldError
import am.leon.utilities.helpers.inputField.InputFieldError
import am.leon.utilities.helpers.inputField.InputFieldNumberRangeError
import am.leon.utilities.helpers.inputField.InputFieldPhoneError
import am.leon.utilities.helpers.inputField.InputFieldPostalCodeError
import am.leon.utilities.helpers.inputField.InputFieldStringCountError
import kotlinx.datetime.LocalDate

interface Validator {

    object General : Validator {

        fun validateIsRequired(isRequired: Boolean): Set<IInputFieldError> = buildSet {
            if (isRequired.not()) add(InputFieldError.REQUIRED)
        }

        fun validateIsInitialState(isInitial: Boolean): Set<IInputFieldError> = buildSet {
            if (isInitial) add(InputFieldError.INITIAL_STATE)
        }
    }

    object Numbers : Validator {

        fun <Value> validatePositive(
            value: Value?, isRequired: Boolean = true
        ): Set<IInputFieldError> where Value : Number, Value : Comparable<Value> = buildSet {
            if (!isRequired && value == null) return@buildSet
            if (isRequired && value == null) add(InputFieldError.REQUIRED)
            if (value != null && value.toDouble() <= 0.0) add(InputFieldError.NUMBER_MUST_BE_POSITIVE)
        }

        fun <Value : Number> validatePositiveOrZero(
            value: Value?, isRequired: Boolean = true
        ): Set<IInputFieldError> = buildSet {
            if (!isRequired && value == null) return@buildSet
            if (isRequired && value == null) add(InputFieldError.REQUIRED)
            if (value != null && value.toDouble() < 0.0) add(InputFieldError.NUMBER_MUST_BE_POSITIVE_OR_ZERO)
        }

        fun <Value : Number> validateNonZero(
            value: Value?, isRequired: Boolean = true
        ): Set<IInputFieldError> = buildSet {
            if (!isRequired && value == null) return@buildSet
            if (isRequired && value == null) add(InputFieldError.REQUIRED)
            if (value != null && value.toDouble() == 0.0) add(InputFieldError.ZERO_IS_NOT_ACCEPTED)
        }

        fun <value : Number> validatePositive(
            values: List<value>?, isRequired: Boolean = true, allowEmpty: Boolean = false
        ): Set<IInputFieldError> = buildSet {
            if (!isRequired && (values == null || allowEmpty && values.isEmpty())) return@buildSet
            if (isRequired && (values == null || !allowEmpty && values.isEmpty())) add(
                InputFieldError.REQUIRED
            )
            if (values != null && values.any { it.toDouble() <= 0.0 }) add(InputFieldError.NUMBER_MUST_BE_POSITIVE)
        }

        fun validateNonZero(value: String?, isRequired: Boolean = true): Set<IInputFieldError> =
            buildSet {
                if (!isRequired && value.isNullOrBlank()) return@buildSet
                if (isRequired && value.isNullOrBlank()) add(InputFieldError.REQUIRED)
                if (isZero(value = value)) add(InputFieldError.ZERO_IS_NOT_ACCEPTED)
            }

        private fun isZero(value: String?): Boolean {
            return value.isNullOrBlank().not() && runCatching {
                value.trim().toInt() == 0
            }.getOrDefault(false)
        }

        fun <Value> validateRange(
            value: Value?, minValue: Value? = null, maxValue: Value? = null,
            inclusiveMin: Boolean = true, inclusiveMax: Boolean = true, isRequired: Boolean = true
        ): Set<IInputFieldError> where Value : Number, Value : Comparable<Value> = buildSet {
            if (!isRequired && value == null) return@buildSet
            if (isRequired && value == null) add(InputFieldError.REQUIRED)

            val isLessThanMin = minValue != null && value != null &&
                    if (inclusiveMin) value < minValue else value <= minValue

            val isMoreThanMax = maxValue != null && value != null &&
                    if (inclusiveMax) value > maxValue else value >= maxValue

            if (isLessThanMin || isMoreThanMax) add(
                InputFieldNumberRangeError.Range(minValue = minValue, maxValue = maxValue)
            )
        }

        fun <Value> validateExactValue(
            value: Value?, exactValue: Value?, isRequired: Boolean = true
        ): Set<IInputFieldError> where Value : Number, Value : Comparable<Value> = buildSet {
            if (!isRequired && value == null) return@buildSet
            if (isRequired && value == null) add(InputFieldError.REQUIRED)
            if (value != null && value != exactValue) add(
                InputFieldNumberRangeError.ExactValue(value = exactValue)
            )
        }
    }

    object Strings : Validator {

        fun validateRange(
            value: String?, minCount: Int? = null, maxCount: Int? = null,
            inclusiveMin: Boolean = true, inclusiveMax: Boolean = true, isRequired: Boolean = true
        ): Set<IInputFieldError> = buildSet {
            if (!isRequired && value.isNullOrBlank()) return@buildSet
            if (isRequired && value.isNullOrBlank()) add(InputFieldError.REQUIRED)

            val isLessThanCount =
                (minCount != null && isLessThanCount(
                    value = value, count = minCount, inclusive = inclusiveMin
                ))

            val isMoreThanCount =
                (maxCount != null && isMoreThanCount(
                    value = value, count = maxCount, inclusive = inclusiveMax
                ))

            if (isLessThanCount || isMoreThanCount) add(
                InputFieldStringCountError.Range(
                    minCount = if (inclusiveMin) minCount else minCount?.plus(1),
                    maxCount = if (inclusiveMax) maxCount else maxCount?.minus(1)
                )
            )
        }

        fun validateExactCount(
            value: String?, count: Int, isRequired: Boolean = true
        ): Set<IInputFieldError> = buildSet {
            if (!isRequired && value.isNullOrBlank()) return@buildSet
            if (isRequired && value.isNullOrBlank()) add(InputFieldError.REQUIRED)
            if (isExactCount(value = value, count = count).not())
                add(InputFieldStringCountError.ExactCount(count = count))
        }

        private fun isLessThanCount(value: String?, count: Int, inclusive: Boolean): Boolean {
            if (value.isNullOrBlank()) return true
            val characterCount = value.trim().count()
            return if (inclusive) characterCount < count else characterCount <= count
        }

        private fun isMoreThanCount(value: String?, count: Int, inclusive: Boolean): Boolean {
            if (value.isNullOrBlank()) return false
            val characterCount = value.trim().count()
            return if (inclusive) characterCount > count else characterCount >= count
        }

        private fun isExactCount(value: String?, count: Int): Boolean {
            return value.isNullOrBlank().not() && value.trim().count() == count
        }
    }

    object Email : Validator {

        fun validate(email: String?, isRequired: Boolean = true): Set<IInputFieldError> = buildSet {
            if (!isRequired && email.isNullOrBlank()) return@buildSet
            if (isRequired && email.isNullOrBlank()) add(InputFieldError.REQUIRED)
            if (isEmailValid(email).not()) add(InputFieldError.EMAIL_IS_NOT_VALID)
        }

        private fun isEmailValid(email: String?): Boolean {
            val validEmailRegex =
                "^(?=.{1,50}$)[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(?:[A-Z0-9](?:[A-Z0-9-]*[A-Z0-9])?\\.)+[A-Z]{2,}$"
                    .toRegex(RegexOption.IGNORE_CASE)
            return email.isNullOrBlank().not() && validEmailRegex.matches(email)
        }
    }

    object Phone : Validator {

        fun validate(
            phoneNumber: String?, countryCode: String? = null,
            isRequired: Boolean = true, isCountryCodeRequired: Boolean = false,
        ): Set<IInputFieldError> = buildSet {
            if (!isRequired && phoneNumber.isNullOrBlank()) return@buildSet
            if (isRequired && phoneNumber.isNullOrBlank()) {
                add(InputFieldError.REQUIRED)
                return@buildSet
            }

            val rule = CountryRules.forCountryCode(countryCode)
            if (rule == null) {
                if (isCountryCodeValid(countryCode, isCountryCodeRequired).not())
                    add(InputFieldPhoneError.CountryCodeRequired)

                if (isPhoneValidAsFallback(phoneNumber).not()) add(InputFieldError.PHONE_IS_NOT_VALID)
                return@buildSet
            }

            if (rule.matchesPhoneNumber(phoneNumber).not()) add(
                isPhoneValid(rule = rule, phoneNumber = phoneNumber)
            )
        }

        private fun isCountryCodeValid(
            countryCode: String?,
            isCountryCodeRequired: Boolean
        ): Boolean {
            return isCountryCodeRequired.not() || countryCode.isNullOrBlank().not()
        }

        private fun isPhoneValid(
            rule: CountryValidationRule,
            phoneNumber: String?
        ): IInputFieldError {
            val digitCount = rule.normalizePhoneNumber(phoneNumber).length
            return if (rule.phoneLengths.isNotEmpty() && digitCount !in rule.phoneLengths)
                InputFieldPhoneError.InvalidDigitCount(
                    expectedLengths = rule.phoneLengths,
                    currentCount = digitCount
                )
            else InputFieldPhoneError.InvalidFormat(
                countryCode = rule.countryCode,
                example = rule.phoneExample
            )
        }

        private fun isPhoneValidAsFallback(phoneNumber: String?): Boolean {
            val validPhoneRegex = Regex("^[0-9]{7,20}$")
            return phoneNumber.isNullOrBlank().not() && validPhoneRegex.matches(phoneNumber)
        }
    }

    object Password : Validator {

        fun validate(password: String?, isRequired: Boolean = true): Set<IInputFieldError> =
            buildSet {
                if (!isRequired && password.isNullOrBlank()) return@buildSet
                if (isRequired && password.isNullOrBlank()) add(InputFieldError.REQUIRED)
                if (withinRange(password).not()) add(InputFieldError.PASSWORD_LENGTH_RANGE)
                if (hasUppercase(password).not()) add(InputFieldError.PASSWORD_UPPERCASE)
                if (hasLowercase(password).not()) add(InputFieldError.PASSWORD_LOWERCASE)
                if (hasNumbers(password).not()) add(InputFieldError.PASSWORD_NUMBER)
                if (hasSpecialChar(password).not()) add(InputFieldError.PASSWORD_SPECIAL_CHAR)
            }

        fun validateConfirmation(
            password: String?, confirmationPassword: String?, isRequired: Boolean = true
        ): Set<IInputFieldError> = buildSet {
            if (!isRequired && password.isNullOrBlank()) return@buildSet
            if (isRequired && confirmationPassword.isNullOrBlank()) add(InputFieldError.REQUIRED)
            if (password != confirmationPassword) add(InputFieldError.REPEATED_PASSWORD_ARE_NOT_THE_SAME)
        }

        private fun withinRange(password: String?): Boolean {
            val validLengthRangeRegex = Regex(".{8,50}")
            return password.isNullOrBlank().not() && password.matches(validLengthRangeRegex)
        }

        private fun hasUppercase(password: String?): Boolean {
            val uppercaseRegex = Regex(".*[A-Z].*")
            return password.isNullOrBlank().not() && uppercaseRegex.matches(password)
        }

        private fun hasLowercase(password: String?): Boolean {
            val lowercaseRegex = Regex(".*[a-z].*")
            return password.isNullOrBlank().not() && lowercaseRegex.matches(password)
        }

        private fun hasNumbers(password: String?): Boolean {
            val numberRegex = Regex(".*[0-9].*")
            return password.isNullOrBlank().not() && numberRegex.matches(password)
        }

        private fun hasSpecialChar(password: String?): Boolean {
            val specialCharRegex = Regex(".*[!@#$%^&*-].*")
            return password.isNullOrBlank().not() && specialCharRegex.matches(password)
        }
    }

    object URL {

        fun validate(url: String?, isRequired: Boolean = true): Set<IInputFieldError> = buildSet {
            if (!isRequired && url.isNullOrBlank()) return@buildSet
            if (isRequired && url.isNullOrBlank()) add(InputFieldError.REQUIRED)
            if (hasWhiteSpaces(url)) add(InputFieldError.WEBSITE_URL_CONTAINS_SPACES)
            if (hasValidProtocol(url).not()) add(InputFieldError.WEBSITE_URL_PROTOCOL_IS_REQUIRED)
            if (hasValidDomain(url).not()) add(InputFieldError.WEBSITE_DOMAIN_IS_NOT_VALID)
        }

        private fun hasWhiteSpaces(url: String?): Boolean {
            return url.isNullOrBlank().not() && url.contains(' ')
        }

        private fun hasValidProtocol(url: String?): Boolean {
            val protocolRegex = Regex("^https?://.*")
            return url.isNullOrBlank().not() && protocolRegex.matches(url)
        }

        private fun hasValidDomain(url: String?): Boolean {
            val validDomainRegex =
                Regex("^https?://(?:www\\.)?[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+(?:/\\S*)?$")
            return url.isNullOrBlank().not() && validDomainRegex.matches(url)
        }
    }

    object PostalCode : Validator {

        fun validate(
            postalCode: String?, countryCode: String? = null, isRequired: Boolean = true
        ): Set<IInputFieldError> = buildSet {
            if (!isRequired && postalCode.isNullOrBlank()) return@buildSet
            if (isRequired && postalCode.isNullOrBlank()) {
                add(InputFieldError.REQUIRED)
                return@buildSet
            }

            val rule = CountryRules.forCountryCode(countryCode)
            if (rule?.hasPostalSystem == false) return@buildSet

            when {
                rule?.postalPattern != null -> if (rule.matchesPostalCode(postalCode).not()) add(
                    InputFieldPostalCodeError.InvalidFormat(
                        countryCode = rule.countryCode, example = rule.postalExample
                    )
                )

                else -> if (isPostalCodeValidAsFallback(postalCode).not()) add(InputFieldError.POSTAL_CODE_IS_NOT_VALID)
            }
        }

        private fun isPostalCodeValidAsFallback(postalCode: String?): Boolean {
            val validPostalCodeRegex = Regex("^[0-9]{3,10}$")
            return postalCode.isNullOrBlank().not() && validPostalCodeRegex.matches(postalCode)
        }
    }

    object Version : Validator {

        fun validate(version: String?, isRequired: Boolean = true): Set<IInputFieldError> =
            buildSet {
                if (!isRequired && version.isNullOrBlank()) return@buildSet
                if (isRequired && version.isNullOrBlank()) add(InputFieldError.REQUIRED)
                if (isVersionValid(version).not()) add(InputFieldError.VERSION_IS_NOT_VALID)
            }

        private fun isVersionValid(version: String?): Boolean {
            val validVersionRegex = Regex("^[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{1,2}$")
            return version.isNullOrBlank().not() && validVersionRegex.matches(version)
        }
    }

    object Location : Validator {

        fun validateLatitude(latitude: String?, isRequired: Boolean = true): Set<IInputFieldError> =
            buildSet {
                if (!isRequired && latitude.isNullOrBlank()) return@buildSet
                if (isRequired && latitude.isNullOrBlank()) add(InputFieldError.REQUIRED)
                if (isLatitudeValid(latitude).not()) add(InputFieldError.LATITUDE_IS_NOT_VALID)
            }

        fun validateLongitude(
            longitude: String?, isRequired: Boolean = true
        ): Set<IInputFieldError> = buildSet {
            if (!isRequired && longitude.isNullOrBlank()) return@buildSet
            if (isRequired && longitude.isNullOrBlank()) add(InputFieldError.REQUIRED)
            if (isLongitudeValid(longitude).not()) add(InputFieldError.LONGITUDE_IS_NOT_VALID)
        }

        private fun isLatitudeValid(latitude: String?): Boolean {
            val validLatitudeRegex = Regex("^[-+]?([0-8]?[0-9](\\.[0-9]+)?|90(\\.0+)?)$")
            return latitude.isNullOrBlank().not() && validLatitudeRegex.matches(latitude)
        }

        private fun isLongitudeValid(longitude: String?): Boolean {
            val validLongitudeRegex =
                Regex("^[-+]?((1[0-7][0-9]|[0-9]{1,2})(\\.[0-9]+)?|180(\\.0+)?)$")
            return longitude.isNullOrBlank().not() && validLongitudeRegex.matches(longitude)
        }
    }

    object Date : Validator {
        fun validateNotAfterToday(
            date: LocalDate?, isRequired: Boolean = true
        ): Set<IInputFieldError> = buildSet {
            if (!isRequired && date == null) return@buildSet
            if (isRequired && date == null) add(InputFieldError.REQUIRED)
            if (date != null && date > LocalDate.now()) add(InputFieldError.DATE_CANNOT_BE_FUTURE)
        }

        fun validateNotBeforeToday(
            date: LocalDate?, isRequired: Boolean = true
        ): Set<IInputFieldError> = buildSet {
            if (!isRequired && date == null) return@buildSet
            if (isRequired && date == null) add(InputFieldError.REQUIRED)
            if (date != null && date < LocalDate.now()) add(InputFieldError.DATE_CANNOT_BE_IN_PAST)
        }

        fun validateRange(from: LocalDate?, to: LocalDate?): Set<IInputFieldError> = buildSet {
            if (from != null && to != null && from > to) add(InputFieldError.INVALID_DATE_RANGE)
        }
    }
}