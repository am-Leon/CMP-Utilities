package am.leon.utilities.helpers.fileSystem.models

data class LeonFile(
    /**
     * The file's name including extension if any.
     */
    val name: String,

    /**
     * Absolute path to the file
     */
    val absolutePath: PathComponent,

    /**
     * Canonical path to the file. May differ from the input
     */
    val canonicalPath: PathComponent,

    /**
     * Date on which file was created.
     *
     * Note: On Android, this is always 0 as creation date is unavailable on all supported versions.
     */
    val createdAt: Double = Double.NaN,

    /**
     * Date when file was last modified.
     */
    val modifiedAt: Double = Double.NaN,

    /**
     * Size of the file in bytes
     */
    val size: Double = Double.NaN,

    /**
     * Present leonFile as byteArray.
     */
    val bytes: ByteArray,

    /**
     * Type of the file.
     */
    val type: FileType,

    /**
     * MimeType of the file.
     */
    val mimeType: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LeonFile

        if (name != other.name) return false
        if (absolutePath != other.absolutePath) return false
        if (canonicalPath != other.canonicalPath) return false
        if (createdAt != other.createdAt) return false
        if (modifiedAt != other.modifiedAt) return false
        if (size != other.size) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + absolutePath.hashCode()
        result = 31 * result + canonicalPath.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + modifiedAt.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}