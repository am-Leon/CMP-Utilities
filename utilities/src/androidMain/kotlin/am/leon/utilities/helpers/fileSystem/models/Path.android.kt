package am.leon.utilities.helpers.fileSystem.models

import android.net.Uri
import java.io.File

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

        val Empty = Path()
        fun urlFromString(urlString: String?) =
            if (urlString != null) Uri.fromFile(File(urlString)) else null

        fun fromUrl(url: Uri?) = Path(url?.path, url?.path)
        fun fromUrlString(urlString: String) = fromUrl(urlFromString(urlString))

        /**
         * Creates a new [Path] with the given [path] argument.
         * [path] is copied to both absolute and relative path components.
         */
        actual fun simplified(path: String): Path = Path(path, path)
    }
}