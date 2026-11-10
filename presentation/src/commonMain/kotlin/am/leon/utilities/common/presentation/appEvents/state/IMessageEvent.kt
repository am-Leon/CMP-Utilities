package am.leon.utilities.common.presentation.appEvents.state

import am.leon.utilities.extentions.IString
import androidx.compose.runtime.Stable

@Stable
sealed interface IMessageEvent {
    val message: IString
    val type: MessageType

    data class Snackbar(
        override val message: IString,
        override val type: MessageType = MessageType.DEFAULT,
        val duration: MessageDuration = MessageDuration.SHORT,
        val isDismissible: Boolean = false,
        val action: MessageAction? = null
    ) : IMessageEvent

    data class Dialog(
        val title: IString? = null,
        override val message: IString,
        override val type: MessageType = MessageType.DEFAULT,
        val confirmAction: MessageAction? = null,
        val dismissAction: MessageAction? = null
    ) : IMessageEvent
}

data class MessageAction(
    val label: IString,
    val action: () -> Unit
)

enum class MessageType { DEFAULT, ERROR, SUCCESS }

enum class MessageDuration { SHORT, LONG, INDEFINITE }