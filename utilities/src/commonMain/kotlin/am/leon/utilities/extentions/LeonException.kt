package am.leon.utilities.extentions

import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.helpers.inputField.IInputFieldKey
import am.leon.utilities.helpers.inputField.InputFieldError
import kotlinx.serialization.json.Json

internal fun LeonException.fromLeonException(json: Json): String {
    return json.encodeToString(serializer = LeonException.serializer(), value = this)
}

fun String.toLeonException(json: Json): LeonException {
    return json.decodeFromString(LeonException.serializer(), this)
}

fun buildRequiredRequestValidation(
    clazz: String?, key: IInputFieldKey
): LeonException.Local.RequestValidation {
    return LeonException.Local.RequestValidation(
        clazz = clazz, validationErrors = mapOf(key to mutableSetOf(InputFieldError.REQUIRED))
    )
}
