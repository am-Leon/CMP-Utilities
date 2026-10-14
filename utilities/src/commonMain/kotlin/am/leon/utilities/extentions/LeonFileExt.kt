package am.leon.utilities.extentions

import am.leon.utilities.helpers.fileSystem.models.FileType
import am.leon.utilities.helpers.fileSystem.models.LeonFile
import am.leon.utilities.helpers.fileSystem.models.PathComponent

/**
 * Returns `true` if this nullable Leon File is either `null` or has empty byte array .
 */
fun LeonFile?.isNullOrEmpty(): Boolean = this == null || this.bytes.isEmpty()

/**
 * Returns `true` if Leon File has empty byte array .
 */
fun LeonFile.isEmpty(): Boolean = this.bytes.isEmpty()

/**
 * Returns `true` if Leon File has empty byte array .
 */
fun LeonFile?.orDefault(): LeonFile = this ?: LeonFile(
    name = "", absolutePath = PathComponent(""), canonicalPath = PathComponent(""),
    createdAt = Double.NaN, modifiedAt = Double.NaN, size = Double.NaN,
    bytes = ByteArray(0), type = FileType.Unknown, mimeType = ""
)