package am.leon.utilities.common.presentation.viewModel.base

import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.common.presentation.viewModel.contract.IAction
import am.leon.utilities.common.presentation.viewModel.contract.IEvent
import am.leon.utilities.common.presentation.viewModel.contract.IState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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

private data class CounterState(val count: Int = 0) : IState
private data object Increment : IAction
private data object Counted : IEvent

private class CounterVM : MviVM<IAction, IEvent, CounterState>(CounterState()) {
    var initCount = 0
        private set

    val recordedAction: IAction?
        get() = lastAction.value

    override fun onInit() {
        initCount++
    }

    override fun handleException(exception: LeonException) = Unit

    override fun clearState() = Unit

    override fun onActionTrigger(action: IAction) {
        emit { copy(count = count + 1) }
        emit(Counted)
    }
}

/**
 * The reason the state machine sits in a layer of its own: covering the whole loop takes no
 * navigator, no event controller and no Koin - the ViewModel under test is built with `CounterVM()`
 * and nothing else.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MviVMTest {

    private val dispatcher = StandardTestDispatcher()

    /**
     * The action collector is started from `init` on the main dispatcher, and the actions are a
     * [kotlinx.coroutines.flow.MutableSharedFlow] with no replay - anything sent before it
     * subscribes is dropped. In the app that subscription happens during construction, because
     * `viewModelScope` dispatches on `Main.immediate`; under a [StandardTestDispatcher] it waits
     * for the scheduler, so let it run before sending anything.
     */
    private fun TestScope.counterVM(): CounterVM = CounterVM().also { advanceUntilIdle() }

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun an_action_reaches_onActionTrigger_and_moves_the_state() = runTest(dispatcher) {
        val viewModel = counterVM()

        viewModel.processIntent(Increment)
        viewModel.processIntent(Increment)
        advanceUntilIdle()

        assertEquals(2, viewModel.viewState.value.count)
    }

    @Test
    fun the_last_action_is_kept_so_a_retry_can_replay_it() = runTest(dispatcher) {
        val viewModel = counterVM()

        viewModel.processIntent(Increment)
        advanceUntilIdle()

        assertEquals(Increment, viewModel.recordedAction)
    }

    @Test
    fun an_event_waits_in_the_buffer_until_something_listens() = runTest(dispatcher) {
        val viewModel = counterVM()

        viewModel.processIntent(Increment)
        advanceUntilIdle()

        // Nothing was collecting when the event was sent. Events go through a buffered Channel
        // rather than a SharedFlow precisely so it is still there for the screen that comes back.
        assertEquals(Counted, viewModel.viewEvent.first())
    }

    @Test
    fun initialize_runs_onInit_only_the_first_time() = runTest(dispatcher) {
        val viewModel = counterVM()

        viewModel.initialize()
        viewModel.initialize()
        advanceUntilIdle()

        assertEquals(1, viewModel.initCount)
    }
}
