@file:OptIn(ExperimentalForeignApi::class)

package am.leon.utilities.helpers.fileSystem.models

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes

fun NSData.toByteArray(): ByteArray? {
    if (bytes == null) {
        return null
    }
    return bytes!!.readBytes(length.toInt())
}

fun ByteArray.toNSData(): NSData? {
    if (isEmpty()) {
        return null
    }

    return usePinned {
        return@usePinned NSData.dataWithBytes(it.addressOf(0), it.get().size.toULong())
    }
}