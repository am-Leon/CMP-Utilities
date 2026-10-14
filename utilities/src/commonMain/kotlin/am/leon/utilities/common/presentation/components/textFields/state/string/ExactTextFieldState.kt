package am.leon.utilities.common.presentation.components.textFields.state.string

import androidx.compose.runtime.Immutable
import am.leon.utilities.common.domain.models.request.Validator
import am.leon.utilities.common.presentation.components.textFields.state.IFieldState
import am.leon.utilities.extentions.IString
import am.leon.utilities.helpers.inputField.IInputFieldError

@Immutable
data class ExactTextFieldState(
    override val value: String = "",
    private val count: Int = 0,
    override val isRequired: Boolean = true,
    override val skipInitialValidation: Boolean = true,
    override val validationErrors: IString? = null,
    override val defaultSupportingText: IString = IString.Empty
) : IFieldState<String> {

    override val realtimeErrors: Set<IInputFieldError>
        get() = Validator.Strings.validateExactCount(
            value = value, count = count, isRequired = isRequired
        )

    override fun bindErrors(validationErrors: IString?): ExactTextFieldState {
        return this.copy(skipInitialValidation = validationErrors == null, validationErrors = validationErrors)
    }

    override fun clearInitialState(value: String): ExactTextFieldState {
        return this.copy(value = value, skipInitialValidation = false, validationErrors = null)
    }
}