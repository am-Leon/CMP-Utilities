package am.leon.utilities.extentions

import android.location.Location
import android.text.Editable
import android.util.Base64
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun Double.formatDecimalByPattern(pattern: String = "#00.00", locale: Locale = Locale.US): String {
    val decimalFormat = DecimalFormat(pattern, DecimalFormatSymbols(locale))
    return decimalFormat.format(this)
}

fun ByteArray.base64Encode(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP)
}

fun String.base64Decode(): ByteArray {
    return Base64.decode(this, Base64.NO_WRAP)
}

fun Location.toFormattedValue(): String {
    return "$latitude/$longitude"
}

fun Editable.toCharArray(): CharArray = CharArray(length) { this[it] }