package am.leon.utilities.common.presentation.components.textFields.state.number

import androidx.compose.runtime.Immutable
import am.leon.utilities.common.domain.models.request.Validator
import am.leon.utilities.common.presentation.components.textFields.state.IFieldState
import am.leon.utilities.extentions.IString
import am.leon.utilities.helpers.inputField.IInputFieldError

@Immutable
data class ExactNumberFieldState<Value>(
    override val value: Value? = null,
    val exactValue: Value? = null,
    override val isRequired: Boolean = true,
    override val skipInitialValidation: Boolean = true,
    override val validationErrors: IString? = null,
    override val defaultSupportingText: IString = IString.Empty,
    val valueString: String = value?.takeIf { it.toDouble() > 0.0 }?.toString().orEmpty()
) : IFieldState<Value?> where Value : Number, Value : Comparable<Value> {

    override val realtimeErrors: Set<IInputFieldError>
        get() = Validator.Numbers.validateExactValue(
            value = value, exactValue = exactValue, isRequired = isRequired
        )

    override fun bindErrors(validationErrors: IString?): ExactNumberFieldState<Value> {
        return this.copy(skipInitialValidation = validationErrors == null, validationErrors = validationErrors)
    }

    override fun clearInitialState(value: Value?): ExactNumberFieldState<Value> {
        return this.copy(value = value, skipInitialValidation = false, validationErrors = null)
    }

    fun clearInitialState(value: Value?, valueString: String): ExactNumberFieldState<Value> {
        return this.copy(
            value = value, valueString = valueString, skipInitialValidation = false, validationErrors = null
        )
    }
}