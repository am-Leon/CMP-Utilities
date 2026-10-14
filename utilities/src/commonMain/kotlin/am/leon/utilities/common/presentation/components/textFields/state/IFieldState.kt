package am.leon.utilities.common.presentation.components.textFields.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import am.leon.utilities.extentions.IString
import am.leon.utilities.extentions.mapToCombinedString
import am.leon.utilities.helpers.inputField.IInputFieldError

@Stable
interface IFieldState<Value> {
    val value: Value?
    val isRequired: Boolean get() = true
    val skipInitialValidation: Boolean
    val validationErrors: IString?
    val realtimeErrors: Set<IInputFieldError> get() = emptySet()
    val defaultSupportingText: IString get() = IString.Empty

    private val _error: IString?
        get() = if (skipInitialValidation) null
        else listOfNotNull(validationErrors, validate()).takeIf { it.isNotEmpty() }?.toSet()?.let(IString::Combined)

    val error: String? @Composable get() = _error?.asString()

    val hasError: Boolean get() = _error != null && !skipInitialValidation

    val supportingText: String @Composable get() = error ?: defaultSupportingText.asString()

    fun validate(): IString.Combined? {
        val errorsSet = realtimeErrors.takeIf { it.isNotEmpty() }
        return errorsSet?.mapToCombinedString()
    }

    fun bindErrors(validationErrors: IString?): IFieldState<Value>

    fun clearInitialState(value: Value): IFieldState<Value> = this
}