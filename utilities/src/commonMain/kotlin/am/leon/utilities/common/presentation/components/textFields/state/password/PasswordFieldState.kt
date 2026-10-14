package am.leon.utilities.common.presentation.components.textFields.state.password

import androidx.compose.runtime.Immutable
import am.leon.utilities.common.domain.models.request.Validator
import am.leon.utilities.common.presentation.components.textFields.state.IFieldState
import am.leon.utilities.extentions.IString
import am.leon.utilities.generated.resources.Res
import am.leon.utilities.generated.resources.at_least_8_characters_and_less_than_50
import am.leon.utilities.generated.resources.at_least_one_lowercase
import am.leon.utilities.generated.resources.at_least_one_number
import am.leon.utilities.generated.resources.at_least_one_special_char
import am.leon.utilities.generated.resources.at_least_one_uppercase
import am.leon.utilities.helpers.inputField.IInputFieldError
import am.leon.utilities.helpers.inputField.InputFieldError

@Immutable
data class PasswordFieldState(
    override val value: String = "",
    override val isRequired: Boolean = true,
    override val skipInitialValidation: Boolean = true,
    override val validationErrors: IString? = null,
    override val defaultSupportingText: IString = IString.Empty
) : IFieldState<String> {

    override val realtimeErrors: Set<IInputFieldError>
        get() = Validator.Password.validate(password = value, isRequired = isRequired)

    override fun bindErrors(validationErrors: IString?): PasswordFieldState {
        return this.copy(skipInitialValidation = validationErrors == null, validationErrors = validationErrors)
    }

    override fun clearInitialState(value: String): PasswordFieldState {
        return this.copy(value = value, skipInitialValidation = false, validationErrors = null)
    }

    val validationState: List<PasswordValidateState>
        get() = listOf(
            PasswordValidateState(
                label = IString.MultiplatformRes(Res.string.at_least_8_characters_and_less_than_50),
                isValid = !realtimeErrors.contains(InputFieldError.PASSWORD_LENGTH_RANGE),
                isEmpty = value.isEmpty()
            ),
            PasswordValidateState(
                label = IString.MultiplatformRes(Res.string.at_least_one_uppercase),
                isValid = !realtimeErrors.contains(InputFieldError.PASSWORD_UPPERCASE),
                isEmpty = value.isEmpty()
            ),
            PasswordValidateState(
                label = IString.MultiplatformRes(Res.string.at_least_one_lowercase),
                isValid = !realtimeErrors.contains(InputFieldError.PASSWORD_LOWERCASE),
                isEmpty = value.isEmpty()
            ),
            PasswordValidateState(
                label = IString.MultiplatformRes(Res.string.at_least_one_number),
                isValid = !realtimeErrors.contains(InputFieldError.PASSWORD_NUMBER),
                isEmpty = value.isEmpty()
            ),
            PasswordValidateState(
                label = IString.MultiplatformRes(Res.string.at_least_one_special_char),
                isValid = !realtimeErrors.contains(InputFieldError.PASSWORD_SPECIAL_CHAR),
                isEmpty = value.isEmpty()
            )
        )
}