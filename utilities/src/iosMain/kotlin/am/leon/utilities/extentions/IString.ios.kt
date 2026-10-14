package am.leon.utilities.extentions

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

actual sealed interface IString {

    @Composable
    actual fun asString(): String
    actual suspend fun asStringBlocking(): String

    actual data class Raw actual constructor(actual val value: String) : IString {
        @Composable
        actual override fun asString(): String = value
        actual override suspend fun asStringBlocking(): String = value
    }

    actual class AndroidRes actual constructor(actual val resourceId: Int, actual vararg val args: Any) : IString {
        @Composable
        actual override fun asString(): String = error(
            "Execution Failed -> [AndroidRes] (ID: $resourceId) cannot be compiled or rendered on iOS targets. " +
                    "Please use [IString.Raw] or [IString.MultiplatformRes] for cross-platform strings."
        )

        actual override suspend fun asStringBlocking(): String = error(
            "Execution Failed -> [AndroidRes] (ID: $resourceId) cannot be compiled or rendered on iOS targets. " +
                    "Please use [IString.Raw] or [IString.MultiplatformRes] for cross-platform strings."
        )
    }

    actual class MultiplatformRes actual constructor(
        actual val resource: StringResource,
        actual vararg val args: Any
    ) : IString {
        @Composable
        actual override fun asString(): String = stringResource(resource, *args)
        actual override suspend fun asStringBlocking(): String {
            return runCatching { getString(resource, *args) }.getOrNull().orEmpty()
        }
    }

    actual data class Combined actual constructor(actual val strings: Set<IString>) : IString {
        @Composable
        @Suppress("SimplifiableCallChain")
        actual override fun asString(): String {
            val config = LocalStringConfig.current
            return when (config.mode) {
                StringConfigMode.FIRST -> strings.firstOrNull()?.asString().orEmpty()
                StringConfigMode.ALL -> strings.map { it.asString() }.joinToString(separator = config.separator)
            }
        }

        actual override suspend fun asStringBlocking(): String {
            val config = StringConfigRegistry.current
            return when (config.mode) {
                StringConfigMode.FIRST -> strings.firstOrNull()?.asStringBlocking().orEmpty()
                StringConfigMode.ALL -> strings.map { it.asStringBlocking() }.joinToString(separator = config.separator)
            }
        }
    }

    actual object Empty : IString {
        @Composable
        actual override fun asString(): String = ""
        actual override suspend fun asStringBlocking(): String = ""
    }
}