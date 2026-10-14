package am.leon.utilities.extentions

fun Int?.orNegative() = this ?: -1
fun Int?.orZero() = this ?: 0

fun Boolean?.orFalse() = this ?: false
fun Boolean?.orTrue() = this ?: true

/**
 * Converts Boolean to Int representation for remote API requests
 * true = 1, false = 0
 */
fun Boolean.toRemoteInt(): Int = if (this) 1 else 0

fun Float?.orZero() = this ?: 0f
fun Float?.orNegative() = this ?: -1f

fun Double?.orZero() = this ?: 0.0
fun Double?.orNegative() = this ?: -1.0

fun Long?.orZero() = this ?: 0L
fun Long?.orNegative() = this ?: -1L