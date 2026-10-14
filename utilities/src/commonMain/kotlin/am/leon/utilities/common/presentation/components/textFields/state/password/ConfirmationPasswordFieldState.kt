package am.leon.utilities.common.presentation.components.textFields.state.password

import androidx.compose.runtime.Immutable
import am.leon.utilities.common.domain.models.request.Validator
import am.leon.utilities.common.presentation.components.textFields.state.IFieldState
import am.leon.utilities.extentions.IString
import am.leon.utilities.generated.resources.Res
import am.leon.utilities.generated.resources.matched
import am.leon.utilities.helpers.inputField.IInputFieldError
import am.leon.utilities.helpers.inputField.InputFieldError

@Immutable
data class ConfirmationPasswordFieldState(
    override val value: String = "",
    private val password: String = "",
    override val isRequired: Boolean = true,
    override val skipInitialValidation: Boolean = true,
    override val validationErrors: IString? = null,
    override val defaultSupportingText: IString = IString.Empty
) : IFieldState<String> {

    override val realtimeErrors: Set<IInputFieldError>
        get() = Validator.Password.validateConfirmation(
            password = password, confirmationPassword = value, isRequired = isRequired
        )

    override fun bindErrors(validationErrors: IString?): ConfirmationPasswordFieldState {
        return this.copy(skipInitialValidation = validationErrors == null, validationErrors = validationErrors)
    }

    override fun clearInitialState(value: String): ConfirmationPasswordFieldState {
        return this.copy(value = value, skipInitialValidation = false, validationErrors = null)
    }

    val validationState: List<PasswordValidateState>
        get() = listOf(
            PasswordValidateState(
                label = IString.MultiplatformRes(Res.string.matched),
                isValid = !realtimeErrors.contains(InputFieldError.REPEATED_PASSWORD_ARE_NOT_THE_SAME),
                isEmpty = value.isEmpty()
            )
        )
}