package am.leon.utilities.extentions

import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSDecimalNumberHandler
import platform.Foundation.NSRoundingMode

actual fun Number.toPlainString(isScaled: Boolean, decimals: Int): String {
    if (toDouble().isFinite().not()) return toString()

    val decimalNumber = NSDecimalNumber(string = toString())

    val rounded = if (isScaled) {
        val handler = NSDecimalNumberHandler(
            roundingMode = NSRoundingMode.NSRoundPlain,
            scale = decimals.toShort(),
            raiseOnExactness = false,
            raiseOnOverflow = false,
            raiseOnUnderflow = false,
            raiseOnDivideByZero = false
        )
        decimalNumber.decimalNumberByRoundingAccordingToBehavior(handler)
    } else decimalNumber

    return rounded.stringValue.stripTrailingZerosPlain()
}

private fun String.stripTrailingZerosPlain(): String {
    if (!contains('.')) return this
    val trimmed = trimEnd('0')
    return if (trimmed.endsWith('.')) trimmed.dropLast(1) else trimmed
}