package am.leon.utilities.helpers.fileSystem.models

enum class FileType {
    /**
     * A regular file like text or pdf file.
     */
    Regular,

    /**
     * A directory.
     */
    Directory,

    /**
     * Platforms may support some special files. All those are marked as [Unknown].
     */
    Unknown
}