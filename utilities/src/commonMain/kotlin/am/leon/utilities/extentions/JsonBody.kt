package am.leon.utilities.extentions

import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.helpers.inputField.IInputFieldKey
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Converts a request body built from `IInputFieldKey`s into JSON.
 *
 * A `RemoteRequest` body is a `Map<IInputFieldKey, Any>`, which Ktor's
 * serializer cannot encode on its own — the keys are objects, and the values are
 * `Any`. This walks it: keys become their `title`, and nested maps, collections
 * and arrays keep their shape.
 *
 * An unsupported type throws rather than being silently stringified, so a
 * mistyped body fails at the call instead of reaching the backend malformed.
 */
fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is JsonElement -> this
    is Map<*, *> -> toJsonElement()
    is Collection<*> -> toJsonElement()
    is Array<*> -> toJsonElement()
    is ByteArray -> toList().toJsonElement()
    is CharArray -> toList().toJsonElement()
    is ShortArray -> toList().toJsonElement()
    is IntArray -> toList().toJsonElement()
    is LongArray -> toList().toJsonElement()
    is FloatArray -> toList().toJsonElement()
    is DoubleArray -> toList().toJsonElement()
    is BooleanArray -> toList().toJsonElement()
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    // Before the Enum branch: a field key is its wire title whether it appears
    // as a map key or as a value, so the two never disagree.
    is IInputFieldKey -> JsonPrimitive(title)
    is Enum<*> -> JsonPrimitive(toString())
    else -> throw LeonException.Network.Unhandled(
        "Api provider: cannot serialize unknown type: ${this::class.simpleName}",
    )
}

private fun Map<*, *>.toJsonElement(): JsonElement = JsonObject(
    buildMap {
        this@toJsonElement.forEach { (key, value) ->
            val name = when (key) {
                is IInputFieldKey -> key.title
                is String -> key
                else -> throw LeonException.Network.Unhandled(
                    "Api provider: a body key must be an IInputFieldKey or a String, got $key",
                )
            }
            put(name, value.toJsonElement())
        }
    },
)

private fun Collection<*>.toJsonElement(): JsonElement = JsonArray(map { it.toJsonElement() })

private fun Array<*>.toJsonElement(): JsonElement = JsonArray(map { it.toJsonElement() })
