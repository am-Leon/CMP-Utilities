package am.leon.utilities.extentions

import java.math.RoundingMode

actual fun Number.toPlainString(isScaled: Boolean, decimals: Int): String {
    if (toDouble().isFinite().not()) return toString()
    return toString().toBigDecimal()
        .let { if (isScaled) it.setScale(decimals, RoundingMode.HALF_UP) else it }
        .stripTrailingZeros()
        .toPlainString()
}