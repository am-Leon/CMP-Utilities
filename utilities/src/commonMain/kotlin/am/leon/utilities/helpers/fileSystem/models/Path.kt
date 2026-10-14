package am.leon.utilities.helpers.fileSystem.models

/**
 * Represents a file system path to a resource
 */
expect class Path() {
    /**
     * Absolute path to the resource
     */
    var absolutePath: PathComponent?
        private set

    /**
     * Relative Path to the resource
     */
    var relativePath: PathComponent?
        private set

    companion object {
        /**
         * Creates a new [Path] with the given [path] argument.
         * [path] is copied to both absolute and relative path components.
         */
        fun simplified(path: String): Path
    }
}