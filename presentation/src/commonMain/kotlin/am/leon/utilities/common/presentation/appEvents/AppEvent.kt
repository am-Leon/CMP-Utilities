package am.leon.utilities.common.presentation.appEvents

import am.leon.utilities.common.presentation.appEvents.state.IMessageEvent

/**
 * Anything [am.leon.utilities.common.presentation.appEvents.controller.IAppEventController]
 * can carry.
 *
 * Apps add events of their own by implementing this - ideally as one `sealed` hierarchy per app,
 * the way [AppEvent] is this library's, so that each side keeps an exhaustive `when` over its own
 * cases and neither has to know the other's:
 *
 * ```
 * sealed interface ShopEvent : IAppEvent {
 *     data class CartUpdated(val count: Int) : ShopEvent
 * }
 *
 * appEventController.events.filterIsInstance<ShopEvent>()
 * ```
 */
interface IAppEvent

/** The events this library raises itself. */
sealed interface AppEvent : IAppEvent {
    data object SessionExpired : AppEvent
    data object SheetDismiss : AppEvent
    data object RefreshRequested : AppEvent
    data class MessageEvent(val message: IMessageEvent) : AppEvent
}
