package am.leon.utilities.helpers.inputField

/**
 * A field name the backend sent that no app-side [IInputFieldKey] matches.
 *
 * Server-side validation can name a field this client has never heard of - a new
 * column, or one only another product sends. Keeping it as a key rather than
 * dropping it means the error still reaches the screen.
 */
data class DynamicInputName(override val title: String) : IInputFieldKey
