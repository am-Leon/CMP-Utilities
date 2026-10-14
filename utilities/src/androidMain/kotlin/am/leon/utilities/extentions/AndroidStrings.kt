package am.leon.utilities.extentions

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html

@SuppressLint("ObsoleteSdkInt")
fun String?.getValueFromHtml(): String? {
    return this.takeIf { it.isNullOrBlank().not() }?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT).toString()
        else
            Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY).toString()
    }
}

fun String.isValidAsVersionNumber(): Boolean {
    val validRegex =
        Regex(pattern = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?\$")
    return validRegex.matches(this)
}