package am.leon.utilities.extentions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toJavaLocalTime
import java.time.format.DateTimeFormatter

actual class LocalDateTimeFormatter private constructor(private val locale: Locale) {

    actual fun format(localDateTime: LocalDateTime, pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale.javaLocale)
        return formatter.format(localDateTime.toJavaLocalDateTime())
    }

    actual fun format(localTime: LocalTime, pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale.javaLocale)
        return formatter.format(localTime.toJavaLocalTime())
    }

    actual fun format(localDate: LocalDate, pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale.javaLocale)
        return formatter.format(localDate.toJavaLocalDate())
    }

    actual companion object {
        actual fun init(locale: Locale): LocalDateTimeFormatter {
            return LocalDateTimeFormatter(locale)
        }
    }
}

actual class Locale private constructor(val javaLocale: java.util.Locale) {
    actual companion object {
        actual fun default(): Locale = Locale(java.util.Locale.getDefault())

        actual fun en(): Locale = Locale(java.util.Locale.ENGLISH)

        actual fun forLanguageTag(languageTag: String): Locale =
            Locale(java.util.Locale.forLanguageTag(languageTag))
    }
}