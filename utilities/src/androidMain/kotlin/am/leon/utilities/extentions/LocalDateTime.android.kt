package am.leon.utilities.extentions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toKotlinLocalTime
import java.time.LocalDateTime as JavaLocalDateTime
import java.time.LocalDate as JavaLocalDate
import java.time.LocalTime as JavaLocalTime

actual typealias PlatformDateTime = JavaLocalDateTime

actual typealias PlatformDate = JavaLocalDate

actual typealias PlatformTime = JavaLocalTime

actual fun LocalDateTime.toPlatformDateTime(): PlatformDateTime = this.toJavaLocalDateTime()

actual fun PlatformDateTime.toLocalDateTime(): LocalDateTime = this.toKotlinLocalDateTime()

actual fun LocalDate.toPlatformDate(): PlatformDate = this.toJavaLocalDate()

actual fun PlatformDate.toLocalDate(): LocalDate = this.toKotlinLocalDate()

actual fun LocalTime.toPlatformTime(): PlatformTime = this.toJavaLocalTime()

actual fun PlatformTime.toLocalTime(): LocalTime = this.toKotlinLocalTime()