package am.leon.utilities.common.presentation.viewModel.base

import am.leon.utilities.common.data.models.Resource
import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.common.presentation.viewModel.contract.IAction
import am.leon.utilities.common.presentation.viewModel.contract.IEvent
import am.leon.utilities.common.presentation.viewModel.contract.IState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private data object InteractorTestState : IState

/**
 * The controller counts callers, so `result` has to signal the indicator on and off exactly once
 * per call. A second "off" would clear a count another screen is still holding.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class InteractorVMLoadingTest {

    private val dispatcher = StandardTestDispatcher()

    private class TestVM : InteractorVM<IAction, IEvent, InteractorTestState>(InteractorTestState) {
        val loadingSignals = mutableListOf<Boolean>()

        override fun emit(isLoading: Boolean) {
            loadingSignals.add(isLoading)
        }

        override fun handleException(exception: LeonException) = Unit
        override fun clearState() = Unit
        override fun onActionTrigger(action: IAction) = Unit

        fun run(
            flow: Flow<Resource<Int>>, manageLoading: Boolean = true, onSuccess: (Int) -> Unit = {}
        ) = flow.result(onSuccess = onSuccess, manageLoading = manageLoading)
    }

    /** What a use case emits for a call that owns the indicator. */
    private fun useCaseFlow(value: Int = 1): Flow<Resource<Int>> = flowOf(
        Resource.loading(loading = true), Resource.success(value), Resource.loading(loading = false)
    )

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun signals_the_indicator_on_once_and_off_once() = runTest(dispatcher) {
        val viewModel = TestVM()

        viewModel.run(useCaseFlow())
        advanceUntilIdle()

        // Not listOf(true, false, false): the upstream's own trailing Progress(false) must not be
        // forwarded on top of the one this operator owns.
        assertEquals(listOf(true, false), viewModel.loadingSignals)
    }

    @Test
    fun two_calls_give_back_exactly_what_they_took() = runTest(dispatcher) {
        val viewModel = TestVM()

        viewModel.run(useCaseFlow(value = 1))
        viewModel.run(useCaseFlow(value = 2))
        advanceUntilIdle()

        assertEquals(2, viewModel.loadingSignals.count { it }, "one 'on' per call")
        assertEquals(2, viewModel.loadingSignals.count { !it }, "one 'off' per call")
    }

    @Test
    fun still_clears_the_indicator_when_onSuccess_throws() = runTest(dispatcher) {
        val viewModel = TestVM()

        viewModel.run(useCaseFlow()) { throw IllegalStateException("boom") }
        advanceUntilIdle()

        assertEquals(listOf(true, false), viewModel.loadingSignals)
    }

    @Test
    fun touches_the_indicator_at_all_only_when_it_owns_it() = runTest(dispatcher) {
        val viewModel = TestVM()

        viewModel.run(useCaseFlow(), manageLoading = false)
        advanceUntilIdle()

        assertTrue(viewModel.loadingSignals.isEmpty())
    }
}
