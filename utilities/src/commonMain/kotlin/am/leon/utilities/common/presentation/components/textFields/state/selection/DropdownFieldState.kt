package am.leon.utilities.common.presentation.components.textFields.state.selection

import androidx.compose.runtime.Immutable
import am.leon.utilities.common.domain.models.domain.IInitialState
import am.leon.utilities.common.domain.models.request.Validator
import am.leon.utilities.common.presentation.components.textFields.state.IFieldState
import am.leon.utilities.extentions.IString
import am.leon.utilities.helpers.inputField.IInputFieldError

@Immutable
data class DropdownFieldState<Model : IInitialState>(
    override val value: Model? = null,
    val items: List<Model> = emptyList(),
    override val isRequired: Boolean = true,
    override val skipInitialValidation: Boolean = true,
    override val validationErrors: IString? = null,
    override val defaultSupportingText: IString = IString.Empty
) : IFieldState<Model?> {

    override val realtimeErrors: Set<IInputFieldError>
        get() = Validator.General.validateIsInitialState(isInitial = value?.isInitialState() == true)

    override fun bindErrors(validationErrors: IString?): DropdownFieldState<Model> {
        return this.copy(skipInitialValidation = validationErrors == null, validationErrors = validationErrors)
    }

    override fun clearInitialState(value: Model?): DropdownFieldState<Model> {
        return this.copy(value = value, skipInitialValidation = false, validationErrors = null)
    }
}