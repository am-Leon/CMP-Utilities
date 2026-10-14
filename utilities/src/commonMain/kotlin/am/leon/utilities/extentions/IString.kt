package am.leon.utilities.extentions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.jetbrains.compose.resources.StringResource

enum class StringConfigMode { FIRST, ALL }

interface IStringConfig {
    val mode: StringConfigMode
    val separator: String
}

internal object DefaultStringConfig : IStringConfig {
    override val mode: StringConfigMode = StringConfigMode.ALL
    override val separator: String = ", "
}

object StringConfigRegistry {
    private var _current by mutableStateOf<IStringConfig>(DefaultStringConfig)

    var current: IStringConfig
        get() = _current
        set(value) {
            _current = value
        }
}

val LocalStringConfig: ProvidableCompositionLocal<IStringConfig> = compositionLocalOf { StringConfigRegistry.current }

@Composable
fun IString?.asString(): String = this?.asString().orEmpty()

suspend fun IString?.asStringBlocking(): String = this?.asStringBlocking().orEmpty()

expect sealed interface IString {

    @Composable
    fun asString(): String
    suspend fun asStringBlocking(): String

    class Raw(value: String) : IString {
        val value: String

        @Composable
        override fun asString(): String
        override suspend fun asStringBlocking(): String
    }

    class AndroidRes(resourceId: Int, vararg args: Any) : IString {
        val resourceId: Int
        val args: Array<out Any>

        @Composable
        override fun asString(): String
        override suspend fun asStringBlocking(): String
    }

    class MultiplatformRes(resource: StringResource, vararg args: Any) : IString {
        val resource: StringResource
        val args: Array<out Any>

        @Composable
        override fun asString(): String
        override suspend fun asStringBlocking(): String
    }

    class Combined(strings: Set<IString>) : IString {
        val strings: Set<IString>

        @Composable
        override fun asString(): String
        override suspend fun asStringBlocking(): String
    }

    object Empty : IString {
        @Composable
        override fun asString(): String
        override suspend fun asStringBlocking(): String
    }
}