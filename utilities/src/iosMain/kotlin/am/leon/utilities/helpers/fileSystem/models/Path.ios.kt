package am.leon.utilities.helpers.fileSystem.models

import platform.Foundation.NSURL

/**
 * Represents a file system path to a resource
 */
actual class Path actual constructor() {

    constructor(absolutePath: String?, relativePath: String?) : this() {
        this.absolutePath = PathComponent(absolutePath)
        this.relativePath = PathComponent(relativePath)
    }

    /**
     * Absolute path to the resource
     */
    actual var absolutePath: PathComponent? = null

    /**
     * Relative Path to the resource
     */
    actual var relativePath: PathComponent? = null

    actual companion object {
        fun urlFromString(urlString: String?) =
            if (urlString != null) NSURL.fileURLWithPath(urlString).standardizedURL else null

        fun fromUrl(url: NSURL?) = Path(url?.path, url?.relativePath)

        fun fromUrlString(urlString: String) = fromUrl(urlFromString(urlString))

        /**
         * Creates a new [Path] with the given [path] argument.
         * [path] is copied to both absolute and relative path components.
         */
        actual fun simplified(path: String): Path = Path(path, path)
    }

    override fun toString(): String {
        return "[absolutePath=$absolutePath, relativePath=$relativePath]"
    }
}