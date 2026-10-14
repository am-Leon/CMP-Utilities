package am.leon.utilities.common.presentation.components.textFields.state

import am.leon.utilities.common.domain.models.request.Validator
import am.leon.utilities.extentions.IString
import am.leon.utilities.extentions.MIN
import am.leon.utilities.helpers.inputField.IInputFieldError
import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

@Immutable
data class DateFieldState(
    override val value: LocalDate? = null,
    override val skipInitialValidation: Boolean = true,
    override val validationErrors: IString? = null,
    override val defaultSupportingText: IString = IString.Empty
) : IFieldState<LocalDate?> {

    val formatedValue: String = value?.toString().orEmpty()

    override val realtimeErrors: Set<IInputFieldError>
        get() = Validator.General.validateIsInitialState(isInitial = value == LocalDate.MIN)

    override fun bindErrors(validationErrors: IString?): DateFieldState {
        return this.copy(
            skipInitialValidation = validationErrors == null,
            validationErrors = validationErrors
        )
    }

    override fun clearInitialState(value: LocalDate?): DateFieldState {
        return this.copy(value = value, skipInitialValidation = false, validationErrors = null)
    }
}