package am.leon.utilities.common.presentation.components.textFields.state

import androidx.compose.runtime.Immutable
import am.leon.utilities.common.domain.models.request.Validator
import am.leon.utilities.extentions.IString
import am.leon.utilities.helpers.country.ICountry
import am.leon.utilities.helpers.inputField.IInputFieldError

@Immutable
data class PhoneFieldState<Country : ICountry>(
    override val value: String = "",
    val countries: List<Country> = emptyList(),
    val selectedCountry: Country? = null,
    override val isRequired: Boolean = true,
    override val skipInitialValidation: Boolean = true,
    override val validationErrors: IString? = null,
    override val defaultSupportingText: IString = IString.Empty
) : IFieldState<String> {

    override val realtimeErrors: Set<IInputFieldError>
        get() = Validator.Phone.validate(
            phoneNumber = value, countryCode = selectedCountry?.countryCode, isRequired = isRequired
        )

    override fun bindErrors(validationErrors: IString?): PhoneFieldState<Country> {
        return this.copy(skipInitialValidation = validationErrors == null, validationErrors = validationErrors)
    }

    fun clearInitialState(value: String, selectedCountry: Country): PhoneFieldState<Country> {
        return this.copy(
            value = value, selectedCountry = selectedCountry,
            skipInitialValidation = false, validationErrors = null
        )
    }
}