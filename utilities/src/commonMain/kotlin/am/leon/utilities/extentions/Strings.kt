package am.leon.utilities.extentions

fun String.getPhoneWithNoZeroAtFirst(): String {
    return this.arabicNumbersToEn().removePrefix("0")
}

fun String.removeNumber(): String {
    return if (this.isNotEmpty()) this.substring(0, this.lastIndex) else this
}

fun String.arabicNumbersToEn(): String = map { character ->
    when (character.code) {
        in 0x0660..0x0669 -> character - (0x0660 - '0'.code.toByte())
        in 0x06F0..0x06F9 -> character - (0x06f0 - '0'.code.toByte())
        else -> character
    }
}.joinToString("")

fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar { it.uppercase() }
}

fun String.capitalizeFirstLetterOfEachWord(): String {
    return this.split(" ").joinToString(" ") { it.capitalizeFirstLetter() }
}