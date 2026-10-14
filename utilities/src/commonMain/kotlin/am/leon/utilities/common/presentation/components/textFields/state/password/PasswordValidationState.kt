package am.leon.utilities.common.presentation.components.textFields.state.password

import androidx.compose.runtime.Immutable
import am.leon.utilities.extentions.IString

@Immutable
data class PasswordValidateState(
    val label: IString,
    val isValid: Boolean,
    val isEmpty: Boolean,
)