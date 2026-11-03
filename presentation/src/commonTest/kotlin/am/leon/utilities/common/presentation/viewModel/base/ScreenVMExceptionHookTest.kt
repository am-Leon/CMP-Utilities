package am.leon.utilities.common.presentation.viewModel.base

import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.common.presentation.appEvents.AppEvent
import am.leon.utilities.common.presentation.appEvents.IAppEvent
import am.leon.utilities.common.presentation.appEvents.controller.AppEventController
import am.leon.utilities.common.presentation.appEvents.controller.IAppEventController
import am.leon.utilities.common.presentation.viewModel.contract.IAction
import am.leon.utilities.common.presentation.viewModel.contract.IEvent
import am.leon.utilities.common.presentation.viewModel.contract.IState
import am.leon.utilities.common.presentation.viewModel.exception.ExceptionHandler
import am.leon.utilities.common.presentation.viewModel.exception.IExceptionHandler
import am.leon.utilities.common.presentation.viewModel.exception.IExceptionHandlerEvent
import am.leon.utilities.extentions.IString
import am.leon.utilities.helpers.inputField.IInputFieldKey
import am.leon.utilities.helpers.inputField.InputFieldError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertTrue

private data object ScreenTestState : IState

private data object Reload : IAction

private data class ShopFieldKey(override val title: String) : IInputFieldKey

private val EMAIL = ShopFieldKey("email")
private val COUPON = ShopFieldKey("coupon")

/** An app's own exception event, in its own closed hierarchy. */
private sealed interface ShopExceptionEvent : IExceptionHandlerEvent {
    data object PaymentRequired : ShopExceptionEvent
}

/**
 * Stands in for the one base ViewModel each app writes: it maps the exception it knows about, binds
 * the field it owns, and presents everything else itself.
 */
private class ShopVM(vmCore: IVMCore) : ScreenVM<IAction, IEvent, ScreenTestState, TestDestination>(
    ScreenTestState, TestDestination, vmCore
) {
    val presented = mutableListOf<IExceptionHandlerEvent>()
    val bound = mutableListOf<Map<IInputFieldKey, IString>>()
    val actions = mutableListOf<IAction>()

    override val boundFieldKeys: Set<IInputFieldKey> = setOf(EMAIL)

    override fun createExceptionHandler(
        onEvent: (IExceptionHandlerEvent) -> Unit
    ): IExceptionHandler = ExceptionHandler(handler = onEvent) { exception ->
        ShopExceptionEvent.PaymentRequired.takeIf {
            exception is LeonException.Client.Unhandled && exception.httpErrorCode == 402
        }
    }

    override fun presentExceptionEvent(event: IExceptionHandlerEvent) {
        presented.add(event)
    }

    override fun bindFieldErrors(errors: Map<IInputFieldKey, IString>) {
        bound.add(errors)
    }

    override fun clearState() = Unit

    override fun onActionTrigger(action: IAction) {
        actions.add(action)
    }

    fun raise(exception: LeonException) = handleException(exception)

    fun retry() = onRetryAction()
}

/**
 * The library names what went wrong; the app decides what the user sees. These lock that line: the
 * only thing [ScreenVM] does with an exception of its own accord is raise
 * [AppEvent.SessionExpired], and nothing it does puts a message on screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ScreenVMExceptionHookTest {

    private val dispatcher = StandardTestDispatcher()
    private val controller: IAppEventController = AppEventController()

    /** Lets the action collector in `init` subscribe before anything is sent - see [MviVMTest]. */
    private fun TestScope.viewModel(): ShopVM =
        ShopVM(VMCore(FakeNavigator(), controller)).also { advanceUntilIdle() }

    private fun validationException() = LeonException.Local.RequestValidation(
        clazz = "CheckoutRequest",
        validationErrors = mapOf(
            EMAIL to setOf(InputFieldError.REQUIRED), COUPON to setOf(InputFieldError.REQUIRED)
        )
    )

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun an_app_s_own_exception_event_reaches_the_view_model() = runTest(dispatcher) {
        val viewModel = viewModel()

        viewModel.raise(LeonException.Client.Unhandled(httpErrorCode = 402))
        advanceUntilIdle()

        assertEquals(ShopExceptionEvent.PaymentRequired, viewModel.presented.single())
    }

    @Test
    fun a_message_the_library_used_to_show_itself_now_reaches_the_app() = runTest(dispatcher) {
        val viewModel = viewModel()

        viewModel.raise(LeonException.Network.Retrial())
        advanceUntilIdle()

        val event = assertIs<IExceptionHandlerEvent.ShowMessage>(viewModel.presented.single())
        assertTrue(event.withRetryAction, "a connection error is the retryable kind")
    }

    @Test
    fun session_expiry_is_raised_app_wide_and_not_presented_per_screen() = runTest(dispatcher) {
        val viewModel = viewModel()
        val events = mutableListOf<IAppEvent>()
        val collector = launch { controller.events.toList(events) }
        advanceUntilIdle()

        viewModel.raise(LeonException.Client.Unauthorized)
        advanceUntilIdle()
        collector.cancel()

        assertEquals<List<IAppEvent>>(listOf(AppEvent.SessionExpired), events)
        // Every ViewModel whose call got the 401 would otherwise react to the same expiry.
        assertTrue(viewModel.presented.isEmpty())
    }

    @Test
    fun the_library_shows_nothing_of_its_own_while_handling_an_exception() = runTest(dispatcher) {
        val viewModel = viewModel()
        val events = mutableListOf<IAppEvent>()
        val collector = launch { controller.events.toList(events) }
        advanceUntilIdle()

        viewModel.raise(LeonException.Network.Retrial())
        viewModel.raise(LeonException.Client.Unauthorized)
        viewModel.raise(validationException())
        advanceUntilIdle()
        collector.cancel()

        assertTrue(
            events.none { it is AppEvent.MessageEvent }, "nothing shown by the library: $events"
        )
    }

    @Test
    fun only_the_fields_this_screen_does_not_bind_reach_the_app() = runTest(dispatcher) {
        val viewModel = viewModel()

        viewModel.raise(validationException())
        advanceUntilIdle()

        assertEquals(
            setOf(EMAIL, COUPON), viewModel.bound.single().keys, "the screen sees them all"
        )
        val leftover =
            assertIs<IExceptionHandlerEvent.HandleValidationErrors>(viewModel.presented.single())
        assertEquals(setOf(COUPON), leftover.errors.keys, "only the one it does not bind")
    }

    @Test
    fun a_retry_replays_the_action_that_failed() = runTest(dispatcher) {
        val viewModel = viewModel()

        viewModel.processIntent(Reload)
        advanceUntilIdle()
        viewModel.retry()
        advanceUntilIdle()

        assertEquals<List<IAction>>(listOf(Reload, Reload), viewModel.actions)
    }
}
