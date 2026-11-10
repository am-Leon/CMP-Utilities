package am.leon.utilities.extentions

import kotlin.math.abs
import kotlin.math.roundToLong

expect fun Number.toPlainString(isScaled: Boolean = true, decimals: Int = 5): String

/** Groups thousands and keeps up to two decimals: 28000.0 → "28,000", 1640.5 → "1,640.5". */
fun Double.toAmountString(): String {
    val cents = (abs(this) * 100).roundToLong()
    val whole = (cents / 100).toString().reversed().chunked(3).joinToString(",").reversed()
    val fraction = (cents % 100).toString().padStart(2, '0').trimEnd('0')
    val sign = if (this < 0 && cents != 0L) "-" else ""
    return if (fraction.isEmpty()) "$sign$whole" else "$sign$whole.$fraction"
}
