package am.leon.utilities.common.presentation.viewModel.base

import am.leon.utilities.common.presentation.appEvents.AppEvent
import am.leon.utilities.common.presentation.appEvents.IAppEvent
import am.leon.utilities.common.presentation.appEvents.controller.AppEventController
import am.leon.utilities.common.presentation.appEvents.controller.IAppEventController
import am.leon.utilities.common.presentation.appEvents.state.IMessageEvent
import am.leon.utilities.common.presentation.appEvents.state.MessageAction
import am.leon.utilities.common.presentation.appEvents.state.MessageType
import am.leon.utilities.common.presentation.viewModel.contract.IAction
import am.leon.utilities.common.presentation.viewModel.contract.IEvent
import am.leon.utilities.common.presentation.viewModel.contract.IState
import am.leon.utilities.common.presentation.viewModel.exception.IExceptionHandlerEvent
import am.leon.utilities.extentions.IString
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
import kotlin.test.assertTrue

private data object MessagingState : IState

private class MessagingVM(vmCore: IVMCore) :
    ScreenVM<IAction, IEvent, MessagingState, TestDestination>(
        MessagingState, TestDestination, vmCore
    ) {
    override fun presentExceptionEvent(event: IExceptionHandlerEvent) = Unit

    override fun clearState() = Unit

    override fun onActionTrigger(action: IAction) = Unit

    fun saved() = emit(message = IString.Raw("Saved"), messageType = MessageType.SUCCESS)

    fun confirmDelete(onConfirm: () -> Unit) = emitDialog(
        message = IString.Raw("Delete this item?"),
        title = IString.Raw("Are you sure?"),
        confirmAction = MessageAction(IString.Raw("Delete"), onConfirm)
    )
}

/**
 * The two surfaces sit on opposite sides of an open bottom sheet, so they treat it differently: a
 * snackbar is drawn below the sheet and has to close it to be seen at all, a dialog is drawn above
 * it and must leave it alone.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ScreenVMMessageTest {

    private val dispatcher = StandardTestDispatcher()
    private val controller: IAppEventController = AppEventController()

    private fun TestScope.viewModel(): MessagingVM =
        MessagingVM(VMCore(FakeNavigator(), controller)).also { advanceUntilIdle() }

    /** The controller's events have no replay, so the collector has to be running first. */
    private fun TestScope.recordAppEvents(into: MutableList<IAppEvent>): Job {
        val job = launch { controller.events.toList(into) }
        advanceUntilIdle()
        return job
    }

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun a_snackbar_closes_the_sheet_first_or_it_would_be_shown_behind_it() = runTest(dispatcher) {
        val viewModel = viewModel()
        val events = mutableListOf<IAppEvent>()
        val collector = recordAppEvents(events)

        viewModel.saved()
        advanceUntilIdle()
        collector.cancel()

        assertEquals(2, events.size, "the dismissal and the message: $events")
        assertEquals(AppEvent.SheetDismiss, events.first(), "and in that order")
        val message = assertIs<AppEvent.MessageEvent>(events.last()).message
        assertIs<IMessageEvent.Snackbar>(message)
    }

    @Test
    fun a_dialog_leaves_the_sheet_where_it_is() = runTest(dispatcher) {
        val viewModel = viewModel()
        val events = mutableListOf<IAppEvent>()
        val collector = recordAppEvents(events)
        var confirmed = false

        viewModel.confirmDelete { confirmed = true }
        advanceUntilIdle()
        collector.cancel()

        assertTrue(events.none { it is AppEvent.SheetDismiss }, "it renders above the sheet")
        val dialog = assertIs<IMessageEvent.Dialog>(
            assertIs<AppEvent.MessageEvent>(events.single()).message
        )
        assertEquals("Delete", assertIs<IString.Raw>(dialog.confirmAction?.label).value)

        dialog.confirmAction?.action?.invoke()
        assertTrue(confirmed, "the action is the app's own, carried through untouched")
    }
}
