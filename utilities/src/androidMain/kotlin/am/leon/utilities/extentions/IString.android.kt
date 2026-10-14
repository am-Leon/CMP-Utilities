package am.leon.utilities.extentions

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource as composeStringResource

fun IString?.asString(context: Context): String = this?.asString(context).orEmpty()

actual sealed interface IString {

    @Composable
    actual fun asString(): String
    fun asString(context: Context): String
    actual suspend fun asStringBlocking(): String

    actual data class Raw actual constructor(actual val value: String) : IString {
        @Composable
        actual override fun asString(): String = value
        override fun asString(context: Context): String = value
        actual override suspend fun asStringBlocking(): String = value
    }

    actual class AndroidRes actual constructor(
        actual val resourceId: Int,
        actual vararg val args: Any
    ) : IString {
        @Composable
        actual override fun asString(): String = stringResource(resourceId, *args)

        override fun asString(context: Context): String {
            return runCatching { context.getString(resourceId, *args) }.getOrNull().orEmpty()
        }

        actual override suspend fun asStringBlocking(): String = error(
            "Invalid Resolution -> [asStringBlocking()] is reserved exclusively for [MultiplatformRes] assets. " +
                    "For native [AndroidRes], please resolve via standard context [asString(context)] instead."
        )
    }

    actual class MultiplatformRes actual constructor(
        actual val resource: StringResource,
        actual vararg val args: Any
    ) : IString {
        @Composable
        actual override fun asString(): String = composeStringResource(resource, *args)

        override fun asString(context: Context): String = error(
            "Context Resolution Failed -> [MultiplatformRes] cannot be extracted using a raw Android Context. " +
                    "Please resolve asynchronously via the suspending method [asStringBlocking()] instead."
        )

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

        override fun asString(context: Context): String {
            val config = StringConfigRegistry.current
            return when (config.mode) {
                StringConfigMode.FIRST -> strings.firstOrNull()?.asString(context).orEmpty()
                StringConfigMode.ALL -> strings.joinToString(
                    separator = config.separator, transform = { it.asString(context) }
                )
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
        override fun asString(context: Context): String = ""
        actual override suspend fun asStringBlocking(): String = ""
    }
}