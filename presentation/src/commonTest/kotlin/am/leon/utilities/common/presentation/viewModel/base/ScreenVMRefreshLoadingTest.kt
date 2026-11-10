package am.leon.utilities.common.presentation.viewModel.base

import am.leon.utilities.common.data.models.Resource
import am.leon.utilities.common.presentation.appEvents.controller.AppEventController
import am.leon.utilities.common.presentation.appEvents.controller.IAppEventController
import am.leon.utilities.common.presentation.viewModel.contract.IAction
import am.leon.utilities.common.presentation.viewModel.contract.IEvent
import am.leon.utilities.common.presentation.viewModel.contract.IState
import am.leon.utilities.common.presentation.viewModel.exception.IExceptionHandlerEvent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private data object RefreshState : IState

/** [onRefresh] starts nothing of its own unless a test hands it a call to start. */
private class RefreshVM(vmCore: IVMCore) :
    ScreenVM<IAction, IEvent, RefreshState, TestDestination>(
        RefreshState, TestDestination, vmCore
    ) {
    var onRefreshCall: (() -> Unit)? = null

    override fun presentExceptionEvent(event: IExceptionHandlerEvent) = Unit
    override fun clearState() = Unit
    override fun onActionTrigger(action: IAction) = Unit
    override fun onRefresh() {
        onRefreshCall?.invoke()
    }

    fun load(source: Flow<Resource<Unit>>) = source.result(onSuccess = {})
}

/** A call that stays in flight until [gate] completes, then succeeds - and ends, as a real one does. */
private fun gatedCall(gate: CompletableDeferred<Unit>) = flow {
    gate.await()
    emit(Resource.Success(Unit))
}

/**
 * A pull-to-refresh and an ordinary call share one ViewModel, and the "off" each one sends looks
 * identical. These pin down that whichever order they finish in, the blocking indicator and the
 * refresh spinner are each released exactly once - never stranded, never dropped early.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ScreenVMRefreshLoadingTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var controller: IAppEventController

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        controller = AppEventController()
        controller.updateDestination(TestDestination)
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() =
        RefreshVM(VMCore(navigator = FakeNavigator(), appEventController = controller))

    @Test
    fun a_refresh_on_its_own_drives_the_spinner_and_not_the_blocking_indicator() =
        runTest(dispatcher) {
            val vm = viewModel()
            advanceUntilIdle() // let the VM's refresh collector subscribe (events has no replay)
            val call = CompletableDeferred<Unit>()
            vm.onRefreshCall = { vm.load(gatedCall(call)) }

            controller.triggerRefresh()
            advanceUntilIdle()
            assertTrue(controller.isRefreshing.value, "the spinner should be up")
            assertFalse(controller.isLoading.value, "a refresh must not raise the blocking dialog")

            call.complete(Unit)
            advanceUntilIdle()
            assertFalse(controller.isRefreshing.value)
            assertFalse(controller.isLoading.value)
        }

    @Test
    fun a_running_call_finishing_after_the_refresh_still_releases_the_indicator() =
        runTest(dispatcher) {
            val vm = viewModel()
            val running = CompletableDeferred<Unit>()
            vm.load(gatedCall(running))
            advanceUntilIdle()
            assertTrue(controller.isLoading.value)

            val refreshCall = CompletableDeferred<Unit>()
            vm.onRefreshCall = { vm.load(gatedCall(refreshCall)) }
            controller.triggerRefresh()
            advanceUntilIdle()

            refreshCall.complete(Unit)
            advanceUntilIdle()
            running.complete(Unit)
            advanceUntilIdle()

            assertFalse(controller.isLoading.value, "the running call's count was never given back")
            assertFalse(controller.isRefreshing.value)
        }

    @Test
    fun the_refresh_finishing_first_does_not_take_the_running_call_s_count() = runTest(dispatcher) {
        val vm = viewModel()
        val running = CompletableDeferred<Unit>()
        vm.load(gatedCall(running))
        advanceUntilIdle()

        val refreshCall = CompletableDeferred<Unit>()
        vm.onRefreshCall = { vm.load(gatedCall(refreshCall)) }
        controller.triggerRefresh()
        advanceUntilIdle()

        refreshCall.complete(Unit)
        advanceUntilIdle()
        assertTrue(
            controller.isLoading.value,
            "the indicator was dropped while the earlier call is still running"
        )

        running.complete(Unit)
        advanceUntilIdle()
        assertFalse(controller.isLoading.value)
    }

    @Test
    fun a_refresh_that_starts_no_call_still_releases_a_running_one() = runTest(dispatcher) {
        val vm = viewModel()
        val running = CompletableDeferred<Unit>()
        vm.load(gatedCall(running))
        advanceUntilIdle()
        assertTrue(controller.isLoading.value)

        // onRefresh() starts nothing - the screen had nothing new to fetch.
        controller.triggerRefresh()
        advanceUntilIdle()

        running.complete(Unit)
        advanceUntilIdle()
        assertFalse(controller.isLoading.value, "a refresh that did nothing swallowed the count")
    }

    @Test
    fun the_spinner_waits_for_every_call_the_refresh_started() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle() // let the VM's refresh collector subscribe (events has no replay)
        val first = CompletableDeferred<Unit>()
        val second = CompletableDeferred<Unit>()
        vm.onRefreshCall = {
            vm.load(gatedCall(first))
            vm.load(gatedCall(second))
        }

        controller.triggerRefresh()
        advanceUntilIdle()
        assertTrue(controller.isRefreshing.value)

        first.complete(Unit)
        advanceUntilIdle()
        assertTrue(controller.isRefreshing.value, "spinner dropped while a refresh call still runs")

        second.complete(Unit)
        advanceUntilIdle()
        assertFalse(controller.isRefreshing.value)
        assertFalse(controller.isLoading.value)
    }
}
