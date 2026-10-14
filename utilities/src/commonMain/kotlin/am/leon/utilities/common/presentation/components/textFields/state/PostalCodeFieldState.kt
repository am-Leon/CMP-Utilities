package am.leon.utilities.common.presentation.components.textFields.state

import androidx.compose.runtime.Immutable
import am.leon.utilities.common.domain.models.request.Validator
import am.leon.utilities.extentions.IString
import am.leon.utilities.helpers.inputField.IInputFieldError

@Immutable
data class PostalCodeFieldState(
    val countryCode: String = "",
    override val value: String = "",
    override val isRequired: Boolean = true,
    override val skipInitialValidation: Boolean = true,
    override val validationErrors: IString? = null,
    override val defaultSupportingText: IString = IString.Empty,
) : IFieldState<String> {

    override val realtimeErrors: Set<IInputFieldError>
        get() = Validator.PostalCode.validate(postalCode = value, countryCode = countryCode, isRequired = isRequired)

    override fun bindErrors(validationErrors: IString?): PostalCodeFieldState {
        return this.copy(skipInitialValidation = validationErrors == null, validationErrors = validationErrors)
    }

    override fun clearInitialState(value: String): PostalCodeFieldState {
        return this.copy(value = value, skipInitialValidation = false, validationErrors = null)
    }
}