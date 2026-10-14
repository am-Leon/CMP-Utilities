package am.leon.utilities.helpers.fileSystem.models

/**
 * A [Path] is made up of [PathComponent]s. For example, a path has [Path.absolutePath] and [Path.relativePath] which are both [PathComponent]s.
 */
expect class PathComponent(component: String?) {

    /**
     * Path used to create the component.
     */
    val component: String?

    /**
     * Create a new [PathComponent] by appending [component] string.
     */
    fun byAppending(component: String): PathComponent?
}