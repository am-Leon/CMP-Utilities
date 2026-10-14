package am.leon.utilities.common.presentation.components.textFields.state.number

import androidx.compose.runtime.Immutable
import am.leon.utilities.common.domain.models.request.Validator
import am.leon.utilities.common.presentation.components.textFields.state.IFieldState
import am.leon.utilities.extentions.IString
import am.leon.utilities.helpers.inputField.IInputFieldError

@Immutable
data class RangeNumberFieldState<Value>(
    private val minValue: Value? = null,
    private val maxValue: Value? = null,
    override val value: Value? = null,
    override val isRequired: Boolean = true,
    override val skipInitialValidation: Boolean = true,
    override val validationErrors: IString? = null,
    override val defaultSupportingText: IString = IString.Empty,
    val valueString: String = value?.takeIf { it.toDouble() > 0.0 }?.toString().orEmpty()
) : IFieldState<Value?> where Value : Number, Value : Comparable<Value> {

    override val realtimeErrors: Set<IInputFieldError>
        get() = Validator.Numbers.validateRange(
            value = value, minValue = minValue, maxValue = maxValue, isRequired = isRequired
        )

    override fun bindErrors(validationErrors: IString?): RangeNumberFieldState<Value> {
        return this.copy(skipInitialValidation = validationErrors == null, validationErrors = validationErrors)
    }

    override fun clearInitialState(value: Value?): RangeNumberFieldState<Value> {
        return this.copy(value = value, skipInitialValidation = false, validationErrors = null)
    }

    fun clearInitialState(value: Value?, valueString: String): RangeNumberFieldState<Value> {
        return this.copy(
            value = value, valueString = valueString, skipInitialValidation = false, validationErrors = null
        )
    }
}