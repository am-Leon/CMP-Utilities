package am.leon.utilities.extentions

import am.leon.utilities.common.domain.models.domain.IInitialState

fun <T> notNull(argument: T?, name: String): T {
    requireNotNull(argument) { "$name may not be null" }
    return argument
}

fun ByteArray.clear() {
    fill(0)
}

fun CharArray.clear() {
    fill('0')
}

fun isEmpty(value: Any?): Boolean {
    return when (value) {
        is String -> value.isEmpty()
        is Collection<*> -> value.isEmpty()
        is Map<*, *> -> value.isEmpty()
        is Int -> (value == 0) or (value == -1)
        is Float -> value.isNaN()
        is Double -> value.isNaN()
        is ByteArray -> value.isEmpty()
        is IInitialState -> value.isInitialState()
        else -> value == null
    }
}
