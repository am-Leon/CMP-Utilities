package am.leon.utilities.resources

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Both libraries ship each string twice: once under `commonMain/composeResources` for Compose
 * Multiplatform consumers, and once under `androidMain/res` for native Android (KMP) consumers
 * that reach for `R.string`. A key present in only one of them is missing for half the callers,
 * and nothing at build time says so - it has already drifted once.
 *
 * This walks the repository rather than one module, so a new module with both resource trees is
 * covered the day it is added.
 */
class StringResourceParityTest {

    private val stringName = Regex("""<string name="([^"]+)"""")

    private fun repositoryRoot(): File {
        var directory: File? = File(System.getProperty("user.dir").orEmpty()).absoluteFile
        while (directory != null && !File(directory, "settings.gradle.kts").isFile) {
            directory = directory.parentFile
        }
        return requireNonNull(directory) {
            "settings.gradle.kts not found above ${System.getProperty("user.dir")}"
        }
    }

    private fun <T : Any> requireNonNull(value: T?, message: () -> String): T =
        value ?: throw AssertionError(message())

    private fun namesIn(file: File): Set<String> =
        stringName.findAll(file.readText()).map { it.groupValues[1] }.toSet()

    @Test
    fun every_compose_string_has_an_android_twin_and_the_other_way_round() {
        val root = repositoryRoot()
        val problems = mutableListOf<String>()
        var comparisons = 0

        root.listFiles().orEmpty().filter { it.isDirectory }.sortedBy { it.name }
            .forEach { module ->
                val composeResources = File(module, "src/commonMain/composeResources")
                if (!composeResources.isDirectory) return@forEach
                val androidRes = File(module, "src/androidMain/res")

                composeResources.listFiles().orEmpty()
                    .filter { it.isDirectory && it.name.startsWith("values") }
                    .sortedBy { it.name }
                    .forEach { locale ->
                        val compose = File(locale, "strings.xml")
                        if (!compose.isFile) return@forEach

                        val android = File(androidRes, "${locale.name}/strings.xml")
                        if (!android.isFile) {
                            problems += "${module.name}/${locale.name}: no androidMain/res twin for ${compose.path}"
                            return@forEach
                        }

                        comparisons++
                        val inCompose = namesIn(compose)
                        val inAndroid = namesIn(android)

                        (inCompose - inAndroid).sorted().forEach {
                            problems += "${module.name}/${locale.name}: '$it' is in composeResources but not in androidMain/res"
                        }
                        (inAndroid - inCompose).sorted().forEach {
                            problems += "${module.name}/${locale.name}: '$it' is in androidMain/res but not in composeResources"
                        }
                    }
            }

        assertTrue(
            comparisons > 0,
            "found no string files to compare under $root - has the layout moved?"
        )
        assertTrue(
            problems.isEmpty(),
            "String resources are out of sync, so CMP and native Android consumers see different keys:\n" +
                    problems.joinToString("\n") { "  - $it" }
        )
    }
}
