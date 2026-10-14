package am.leon.utilities.extentions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atDate
import kotlinx.datetime.atTime
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual class LocalDateTimeFormatter private constructor(private val locale: Locale) {

    actual fun format(localDateTime: LocalDateTime, pattern: String): String {
        val formatter = NSDateFormatter().apply {
            dateFormat = pattern
            locale = this@LocalDateTimeFormatter.locale.nsLocale
        }
        val date = localDateTime.toInstant().toNSDate()
        return formatter.stringFromDate(date)
    }

    actual fun format(localTime: LocalTime, pattern: String): String {
        val dateTime = localTime.atDate(LocalDate.now())
        return format(dateTime, pattern)
    }

    actual fun format(localDate: LocalDate, pattern: String): String {
        val dateTime = localDate.atTime(LocalTime.MIN)
        return format(dateTime, pattern)
    }

    actual companion object {
        actual fun init(locale: Locale): LocalDateTimeFormatter {
            return LocalDateTimeFormatter(locale)
        }
    }
}

actual class Locale private constructor(val nsLocale: NSLocale) {
    actual companion object {
        actual fun default(): Locale = Locale(NSLocale.currentLocale())

        actual fun en(): Locale = forLanguageTag("en")

        actual fun forLanguageTag(languageTag: String): Locale = Locale(NSLocale(languageTag))
    }
}