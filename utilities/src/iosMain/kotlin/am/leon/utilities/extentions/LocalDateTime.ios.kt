package am.leon.utilities.extentions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDate
import kotlin.time.ExperimentalTime

actual typealias PlatformDateTime = NSDate

actual typealias PlatformDate = NSDate

actual typealias PlatformTime = NSDate

actual fun LocalDateTime.toPlatformDateTime(): PlatformDateTime =
    this.toInstant(TimeZone.currentSystemDefault()).toNSDate()

actual fun PlatformDateTime.toLocalDateTime(): LocalDateTime =
    this.toKotlinInstant().toLocalDateTime(TimeZone.currentSystemDefault())

actual fun LocalDate.toPlatformDate(): PlatformDate = this.atStartOfDayIn(TimeZone.UTC).toNSDate()

actual fun PlatformDate.toLocalDate(): LocalDate = this.toKotlinInstant().toLocalDateTime(TimeZone.UTC).date

actual fun LocalTime.toPlatformTime(): PlatformTime =
    this.atDate(LocalDate.now()).toInstant(TimeZone.currentSystemDefault()).toNSDate()

actual fun PlatformTime.toLocalTime(): LocalTime =
    this.toKotlinInstant().toLocalDateTime(TimeZone.currentSystemDefault()).time