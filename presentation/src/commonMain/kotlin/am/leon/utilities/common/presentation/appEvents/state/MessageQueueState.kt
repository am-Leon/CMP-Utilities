package am.leon.utilities.common.presentation.appEvents.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Holds one [IMessageEvent] at a time and queues the rest, so a second message waits its turn
 * instead of replacing the one on screen.
 *
 * The surface itself is the app's: each product styles its snackbar and dialog with its own design
 * system, so the host composable that reads [current], shows it and calls [dismiss] lives there,
 * not here.
 *
 * **One instance per surface, not one for all of them.** A modal dialog and a transient snackbar
 * are meant to coexist; sharing a queue would let an `INDEFINITE` snackbar sitting in [current]
 * hold a dialog back behind it. Hence, the aliases below rather than a single shared holder.
 */
@Stable
class MessageQueueState<Event : IMessageEvent> {
    private val queue = mutableListOf<Event>()

    var current by mutableStateOf<Event?>(null)
        private set

    fun show(event: Event) {
        if (current == null) current = event else queue.add(event)
    }

    fun dismiss() {
        current = if (queue.isNotEmpty()) queue.removeAt(0) else null
    }
}

/**
 * Queues [IMessageEvent.Snackbar]s. Keeps [MessageQueueState.current] typed as a snackbar, so the
 * host composable needs no cast.
 */
typealias SnackbarState = MessageQueueState<IMessageEvent.Snackbar>

/**
 * Queues [IMessageEvent.Dialog]s. Keeps [MessageQueueState.current] typed as a dialog, so the host
 * composable needs no cast.
 */
typealias AlertDialogState = MessageQueueState<IMessageEvent.Dialog>
