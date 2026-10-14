package am.leon.utilities.extentions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

expect class PlatformDateTime

expect class PlatformDate

expect class PlatformTime

expect fun LocalDateTime.toPlatformDateTime(): PlatformDateTime

expect fun PlatformDateTime.toLocalDateTime(): LocalDateTime

expect fun LocalDate.toPlatformDate(): PlatformDate

expect fun PlatformDate.toLocalDate(): LocalDate

expect fun LocalTime.toPlatformTime(): PlatformTime

expect fun PlatformTime.toLocalTime(): LocalTime

val LocalTime.Companion.MIN
    get() = LocalTime(0, 0, 0, 999999999)
val LocalTime.Companion.MAX
    get() = LocalTime(23, 59, 59, 999999999)

val LocalDate.Companion.MIN
    get() = LocalDate(0, 1, 1)
val LocalDate.Companion.MAX
    get() = LocalDate(9999, 12, 31)

val LocalDateTime.Companion.MIN
    get() = LocalDateTime(LocalDate.MIN, LocalTime.MIN)
val LocalDateTime.Companion.MAX
    get() = LocalDateTime(LocalDate.MAX, LocalTime.MAX)

val Instant.Companion.MIN
    get() = LocalDateTime.MIN.toInstant(TimeZone.currentSystemDefault())
val Instant.Companion.MAX
    get() = LocalDateTime.MAX.toInstant(TimeZone.currentSystemDefault())

fun LocalDateTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return Clock.System.now().toLocalDateTime(timeZone)
}

fun LocalDate.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
    return LocalDateTime.now(timeZone).date
}

fun LocalTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime {
    return LocalDateTime.now(timeZone).time
}

fun Instant?.toLocalDateTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault(), default: LocalDateTime = LocalDateTime.MIN
): LocalDateTime =
    runCatching { this?.toLocalDateTime(timeZone) ?: default }.getOrDefault(default)

fun Instant?.toLocalDate(
    timeZone: TimeZone = TimeZone.currentSystemDefault(), default: LocalDate = LocalDate.MIN
): LocalDate =
    runCatching { this?.toLocalDateTime(timeZone)?.date ?: default }.getOrDefault(default)

fun Instant?.toLocalTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault(), default: LocalTime = LocalTime.MIN
): LocalTime =
    runCatching { this?.toLocalDateTime(timeZone)?.time ?: default }.getOrDefault(default)

fun LocalDateTime.toInstant(timeZone: TimeZone = TimeZone.currentSystemDefault()): Instant =
    runCatching { this.toInstant(timeZone) }.getOrDefault(Instant.MIN)

fun LocalDate.toEpochMilliseconds(
    timeZone: TimeZone = TimeZone.UTC, default: Instant = Instant.MIN
): Long =
    runCatching { this.atStartOfDayIn(timeZone).toEpochMilliseconds() }.getOrDefault(default.toEpochMilliseconds())

fun LocalTime.toEpochMilliseconds(default: Instant = Instant.MIN): Long =
    runCatching { this.atDate(LocalDate.MIN).toEpochMilliseconds() }.getOrDefault(default.toEpochMilliseconds())

fun LocalDateTime.toEpochMilliseconds(
    timeZone: TimeZone = TimeZone.UTC, default: Instant = Instant.MIN
): Long =
    runCatching { this.toInstant(timeZone).toEpochMilliseconds() }.getOrDefault(default.toEpochMilliseconds())

fun LocalDateTime.Companion.fromString(
    isoString: String?, default: LocalDateTime = LocalDateTime.MIN
): LocalDateTime = runCatching { LocalDateTime.parse(isoString.orEmpty()) }.getOrDefault(default)

fun LocalDate.Companion.fromString(
    isoString: String?, default: LocalDate = LocalDate.MIN
): LocalDate = runCatching { LocalDate.parse(isoString.orEmpty()) }.getOrDefault(default)

fun LocalTime.Companion.fromString(
    isoString: String?, default: LocalTime = LocalTime.MIN
): LocalTime = runCatching { LocalTime.parse(isoString.orEmpty()) }.getOrDefault(default)

fun Instant.Companion.fromString(
    isoString: String?, default: Instant = Instant.MIN
): Instant = runCatching { Instant.parse(isoString.orEmpty()) }.getOrDefault(default)