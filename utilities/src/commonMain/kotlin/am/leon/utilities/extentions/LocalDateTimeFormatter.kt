package am.leon.utilities.extentions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

expect class LocalDateTimeFormatter {
    fun format(localDateTime: LocalDateTime, pattern: String = "EEEE, dd/MM/yyyy, hh:mm a"): String
    fun format(localTime: LocalTime, pattern: String = "hh:mm a"): String
    fun format(localDate: LocalDate, pattern: String = "EEEE, dd/MM/yyyy"): String

    companion object {
        fun init(locale: Locale = Locale.default()): LocalDateTimeFormatter
    }
}

expect class Locale {
    companion object {
        fun default(): Locale
        fun en(): Locale
        fun forLanguageTag(languageTag: String): Locale
    }
}