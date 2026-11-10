package am.leon.utilities.common.presentation.viewModel.base

import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.common.presentation.appEvents.AppEvent
import am.leon.utilities.common.presentation.appEvents.IAppEvent
import am.leon.utilities.common.presentation.appEvents.controller.AppEventController
import am.leon.utilities.common.presentation.appEvents.controller.IAppEventController
import am.leon.utilities.common.presentation.appEvents.state.IMessageEvent
import am.leon.utilities.common.presentation.appEvents.state.MessageDuration
import am.leon.utilities.common.presentation.appEvents.state.MessageType
import am.leon.utilities.extentions.IString
import am.leon.utilities.helpers.inputField.InputFieldError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Proves the reference implementation in [AppBaseVM] actually closes the loop: an exception goes in
 * at the ViewModel and a message comes out on the controller, with nothing in the library deciding
 * what it looks like.
 */
private class FakeAnalytics : Analytics {
    val events = mutableListOf<String>()
    override fun event(name: String) {
        events.add(name)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class AppBaseVMExampleTest {

    private val dispatcher = StandardTestDispatcher()
    private val controller: IAppEventController = AppEventController()

    private val analytics = FakeAnalytics()

    private fun TestScope.viewModel(): CheckoutVM =
        CheckoutVM(AppVMCore(core = VMCore(FakeNavigator(), controller), analytics = analytics))
            .also { advanceUntilIdle() }

    /** The controller's events have no replay, so the collector has to be running first. */
    private fun TestScope.recordAppEvents(into: MutableList<IAppEvent>): Job {
        val job = launch { controller.events.toList(into) }
        advanceUntilIdle()
        return job
    }

    private fun messagesIn(events: List<IAppEvent>): List<IMessageEvent> =
        events.filterIsInstance<AppEvent.MessageEvent>().map { it.message }

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun a_connection_error_becomes_the_app_s_retryable_snackbar() = runTest(dispatcher) {
        val viewModel = viewModel()
        val events = mutableListOf<IAppEvent>()
        val collector = recordAppEvents(events)

        viewModel.raise(LeonException.Network.Retrial())
        advanceUntilIdle()
        collector.cancel()

        val snackbar = assertIs<IMessageEvent.Snackbar>(messagesIn(events).single())
        assertEquals(MessageType.ERROR, snackbar.type)
        assertEquals(MessageDuration.INDEFINITE, snackbar.duration, "it stays until retried")
        assertEquals("Try again", assertIs<IString.Raw>(snackbar.action?.label).value)
    }

    @Test
    fun a_field_the_screen_binds_itself_never_becomes_a_message() = runTest(dispatcher) {
        val viewModel = viewModel()
        val events = mutableListOf<IAppEvent>()
        val collector = recordAppEvents(events)

        viewModel.raise(
            LeonException.Local.RequestValidation(
                clazz = "CheckoutRequest",
                validationErrors = mapOf(CheckoutField.Email to setOf(InputFieldError.REQUIRED))
            )
        )
        advanceUntilIdle()
        collector.cancel()

        assertTrue(viewModel.emailError != null, "it went to the field")
        assertTrue(messagesIn(events).isEmpty(), "and nowhere else")
    }

    @Test
    fun a_field_it_does_not_bind_falls_through_to_one_message() = runTest(dispatcher) {
        val viewModel = viewModel()
        val events = mutableListOf<IAppEvent>()
        val collector = recordAppEvents(events)

        viewModel.raise(
            LeonException.Local.RequestValidation(
                clazz = "CheckoutRequest",
                validationErrors = mapOf(CheckoutField.Coupon to setOf(InputFieldError.REQUIRED))
            )
        )
        advanceUntilIdle()
        collector.cancel()

        assertNull(viewModel.emailError)
        val snackbar = assertIs<IMessageEvent.Snackbar>(messagesIn(events).single())
        assertEquals(MessageDuration.LONG, snackbar.duration)
        assertIs<IString.Combined>(snackbar.message, "kept as resources, not flattened to text")
    }

    @Test
    fun a_dependency_the_library_knows_nothing_about_reaches_every_screen() = runTest(dispatcher) {
        // AppVMCore carries it beside the library's two, and AppBaseVM hands it down, so no screen
        // has to be told about it or reach for it.
        val viewModel = viewModel()

        viewModel.raise(LeonException.Client.Unhandled(httpErrorCode = 402))
        advanceUntilIdle()

        assertEquals(listOf("paywall_shown"), analytics.events)
    }

    @Test
    fun the_app_s_own_case_reaches_the_app_s_own_hook() = runTest(dispatcher) {
        val viewModel = viewModel()
        val events = mutableListOf<IAppEvent>()
        val collector = recordAppEvents(events)

        viewModel.raise(LeonException.Client.Unhandled(httpErrorCode = 402))
        advanceUntilIdle()
        collector.cancel()

        assertTrue(viewModel.paywallShown)
        assertTrue(messagesIn(events).isEmpty(), "a paywall is not a snackbar")
    }
}
