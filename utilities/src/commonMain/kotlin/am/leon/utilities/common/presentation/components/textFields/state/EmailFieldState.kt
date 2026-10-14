package am.leon.utilities.common.presentation.components.textFields.state

import androidx.compose.runtime.Immutable
import am.leon.utilities.common.domain.models.request.Validator
import am.leon.utilities.extentions.IString
import am.leon.utilities.helpers.inputField.IInputFieldError

@Immutable
data class EmailFieldState(
    override val value: String = "",
    override val isRequired: Boolean = true,
    override val skipInitialValidation: Boolean = true,
    override val validationErrors: IString? = null,
    override val defaultSupportingText: IString = IString.Empty
) : IFieldState<String> {

    override val realtimeErrors: Set<IInputFieldError>
        get() = Validator.Email.validate(email = value, isRequired = isRequired)

    override fun bindErrors(validationErrors: IString?): EmailFieldState {
        return this.copy(skipInitialValidation = validationErrors == null, validationErrors = validationErrors)
    }

    override fun clearInitialState(value: String): EmailFieldState {
        return this.copy(value = value, skipInitialValidation = false, validationErrors = null)
    }
}