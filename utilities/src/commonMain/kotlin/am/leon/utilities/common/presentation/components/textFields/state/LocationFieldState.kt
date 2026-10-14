package am.leon.utilities.common.presentation.components.textFields.state

import androidx.compose.runtime.Immutable
import am.leon.utilities.common.domain.models.request.Validator
import am.leon.utilities.extentions.IString
import am.leon.utilities.helpers.inputField.IInputFieldError

@Immutable
data class LocationFieldState(
    val locationType: LocationType = LocationType.LATITUDE,
    override val value: String = "",
    override val isRequired: Boolean = true,
    override val skipInitialValidation: Boolean = true,
    override val validationErrors: IString? = null,
    override val defaultSupportingText: IString = IString.Empty
) : IFieldState<String> {

    enum class LocationType { LATITUDE, LONGITUDE }

    override val realtimeErrors: Set<IInputFieldError>
        get() = when (locationType) {
            LocationType.LATITUDE -> Validator.Location.validateLatitude(value, isRequired)
            LocationType.LONGITUDE -> Validator.Location.validateLongitude(value, isRequired)
        }

    override fun bindErrors(validationErrors: IString?): LocationFieldState {
        return this.copy(skipInitialValidation = validationErrors == null, validationErrors = validationErrors)
    }

    override fun clearInitialState(value: String): LocationFieldState {
        return this.copy(value = value, skipInitialValidation = false, validationErrors = null)
    }
}