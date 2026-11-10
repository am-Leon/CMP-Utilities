package am.leon.utilities.common.presentation.appEvents

import am.leon.utilities.common.presentation.appEvents.controller.AppEventController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** An app's own events, as a closed hierarchy of its own hanging off [IAppEvent]. */
private sealed interface ShopEvent : IAppEvent {
    data class CartUpdated(val count: Int) : ShopEvent
    data object CheckoutFinished : ShopEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
class AppEventExtensionTest {

    @Test
    fun the_controller_carries_an_app_s_own_events_alongside_the_library_s() = runTest {
        val controller = AppEventController()
        val received = mutableListOf<IAppEvent>()

        val job = launch { controller.events.collect { received.add(it) } }
        runCurrent() // the stream has no replay, so subscribe before emitting

        controller.tryEmit(ShopEvent.CartUpdated(count = 3))
        controller.tryEmit(AppEvent.RefreshRequested)
        controller.tryEmit(ShopEvent.CheckoutFinished)
        runCurrent()

        assertEquals(
            listOf(ShopEvent.CartUpdated(count = 3), AppEvent.RefreshRequested, ShopEvent.CheckoutFinished),
            received
        )
        job.cancel()
    }

    @Test
    fun an_app_can_filter_down_to_its_own_hierarchy_and_switch_exhaustively() = runTest {
        val controller = AppEventController()
        val seen = mutableListOf<String>()

        val job = launch {
            controller.events.filterIsInstance<ShopEvent>().collect { event ->
                // Exhaustive: ShopEvent is sealed, so adding a case breaks this on purpose.
                seen.add(
                    when (event) {
                        is ShopEvent.CartUpdated -> "cart:${event.count}"
                        ShopEvent.CheckoutFinished -> "checkout"
                    }
                )
            }
        }
        runCurrent()

        controller.tryEmit(AppEvent.SessionExpired) // the library's own, filtered out
        controller.tryEmit(ShopEvent.CartUpdated(count = 1))
        controller.tryEmit(ShopEvent.CheckoutFinished)
        runCurrent()

        assertEquals(listOf("cart:1", "checkout"), seen)
        job.cancel()
    }
}
