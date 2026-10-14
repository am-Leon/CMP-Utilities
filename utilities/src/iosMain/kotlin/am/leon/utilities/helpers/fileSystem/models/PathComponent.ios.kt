package am.leon.utilities.helpers.fileSystem.models

import platform.Foundation.NSURL

/**
 * A [Path] is made up of [PathComponent]s. For example, a path has [Path.absolutePath] and [Path.relativePath] which are both [PathComponent]s.
 */
actual class PathComponent actual constructor(actual val component: String?) {
    /**
     * Path used to create the component.
     */
    val url = if (component != null) NSURL.fileURLWithPath(component).standardizedURL else null

    /**
     * Create a new [PathComponent] by appending [component] string.
     */
    actual fun byAppending(component: String): PathComponent? {
        if (url == null) return null
        return PathComponent(url.URLByAppendingPathComponent(component)?.path)
    }

    override fun toString(): String {
        return "[component=$component, url=$url]"
    }
}