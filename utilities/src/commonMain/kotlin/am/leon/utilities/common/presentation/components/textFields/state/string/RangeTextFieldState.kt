package am.leon.utilities.common.presentation.components.textFields.state.string

import androidx.compose.runtime.Immutable
import am.leon.utilities.common.domain.models.request.Validator
import am.leon.utilities.common.presentation.components.textFields.state.IFieldState
import am.leon.utilities.extentions.IString
import am.leon.utilities.helpers.inputField.IInputFieldError

@Immutable
data class RangeTextFieldState(
    override val value: String = "",
    private val minCount: Int? = null,
    private val maxCount: Int? = null,
    private val inclusiveMin: Boolean = true,
    private val inclusiveMax: Boolean = true,
    override val isRequired: Boolean = true,
    override val skipInitialValidation: Boolean = true,
    override val validationErrors: IString? = null,
    override val defaultSupportingText: IString = IString.Empty
) : IFieldState<String> {

    override val realtimeErrors: Set<IInputFieldError>
        get() = Validator.Strings.validateRange(
            value = value, minCount = minCount, maxCount = maxCount, inclusiveMin = inclusiveMin,
            inclusiveMax = inclusiveMax, isRequired = isRequired
        )

    override fun bindErrors(validationErrors: IString?): RangeTextFieldState {
        return this.copy(skipInitialValidation = validationErrors == null, validationErrors = validationErrors)
    }

    override fun clearInitialState(value: String): RangeTextFieldState {
        return this.copy(value = value, skipInitialValidation = false, validationErrors = null)
    }
}