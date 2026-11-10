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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private data object CombineTestState : IState

/**
 * `combine` brackets the global indicator from `onStart`/`onCompletion` placed *outside*
 * `collectLatest`, because the combined flow re-emits on every upstream change and clearing from
 * inside would hand back one "off" per emission for a single "on". These pin that, the positional
 * contract the typed overloads rest on, and the fact that [onLoading] is not tied to who owns the
 * indicator.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class InteractorVMCombineTest {

    private val dispatcher = StandardTestDispatcher()

    private class TestVM : InteractorVM<IAction, IEvent, CombineTestState>(CombineTestState) {
        /** What the global indicator was told. */
        val loadingSignals = mutableListOf<Boolean>()

        /** What the caller's own `onLoading` hook was told. */
        val reportedLoading = mutableListOf<Boolean>()

        val handled = mutableListOf<LeonException>()

        override fun emit(isLoading: Boolean) {
            loadingSignals.add(isLoading)
        }

        override fun handleException(exception: LeonException) {
            handled.add(exception)
        }

        override fun clearState() = Unit
        override fun onActionTrigger(action: IAction) = Unit

        fun runTwo(
            first: Flow<Resource<Int>>,
            second: Flow<Resource<String>>,
            manageLoading: Boolean = true,
            onSuccess: (Int?, String?) -> Unit = { _, _ -> },
            onFailure: ((LeonException?, LeonException?) -> Unit)? = null,
        ) = combine(
            firstFlow = first,
            secondFlow = second,
            onSuccess = onSuccess,
            onFailure = onFailure,
            onLoading = { reportedLoading.add(it) },
            manageLoading = manageLoading
        )

        fun runMany(
            vararg flows: Flow<Resource<Any?>>, onSuccess: (List<Any?>) -> Unit = {}
        ) = combine(flows = flows, onSuccess = onSuccess)
    }

    /** What a use case emits for a call that succeeds. */
    private fun <T> useCaseFlow(value: T): Flow<Resource<T>> = flowOf(
        Resource.loading(loading = true), Resource.success(value), Resource.loading(loading = false)
    )

    private fun <T> failingFlow(exception: LeonException): Flow<Resource<T>> = flowOf(
        Resource.loading(loading = true),
        Resource.failure(exception),
        Resource.loading(loading = false)
    )

    /** A call that ends without ever producing a result - every emission is filtered away. */
    private fun <T> progressOnlyFlow(): Flow<Resource<T>> = flowOf(
        Resource.loading(loading = true), Resource.loading(loading = false)
    )

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    // --- the loading bracket -------------------------------------------------------------------

    @Test
    fun signals_the_indicator_on_once_and_off_once() = runTest(dispatcher) {
        val viewModel = TestVM()

        viewModel.runTwo(useCaseFlow(1), useCaseFlow("a"))
        advanceUntilIdle()

        assertEquals(listOf(true, false), viewModel.loadingSignals)
    }

    @Test
    fun takes_one_bracket_however_many_flows_it_was_given() = runTest(dispatcher) {
        val viewModel = TestVM()

        viewModel.runMany(useCaseFlow(1), useCaseFlow(2), useCaseFlow(3), useCaseFlow(4))
        advanceUntilIdle()

        // Not one per flow, and not one per combined emission: the bracket belongs to the call.
        assertEquals(listOf(true, false), viewModel.loadingSignals)
    }

    @Test
    fun still_clears_the_indicator_when_onSuccess_throws() = runTest(dispatcher) {
        val viewModel = TestVM()

        viewModel.runTwo(useCaseFlow(1), useCaseFlow("a")) { _, _ ->
            throw IllegalStateException("boom")
        }
        advanceUntilIdle()

        assertEquals(listOf(true, false), viewModel.loadingSignals)
    }

    @Test
    fun still_clears_the_indicator_when_a_flow_ends_without_a_result() = runTest(dispatcher) {
        val viewModel = TestVM()
        var reachedSuccess = false

        viewModel.runTwo(progressOnlyFlow(), useCaseFlow("a")) { _, _ -> reachedSuccess = true }
        advanceUntilIdle()

        // combine needs every flow to emit once, so onSuccess never runs - the indicator must not
        // be stranded on because of it.
        assertFalse(reachedSuccess, "nothing to report")
        assertEquals(listOf(true, false), viewModel.loadingSignals)
    }

    @Test
    fun touches_the_indicator_at_all_only_when_it_owns_it() = runTest(dispatcher) {
        val viewModel = TestVM()

        viewModel.runTwo(useCaseFlow(1), useCaseFlow("a"), manageLoading = false)
        advanceUntilIdle()

        assertTrue(viewModel.loadingSignals.isEmpty())
    }

    @Test
    fun tells_the_caller_about_loading_even_when_it_does_not_own_the_indicator() =
        runTest(dispatcher) {
            val viewModel = TestVM()

            viewModel.runTwo(useCaseFlow(1), useCaseFlow("a"), manageLoading = false)
            advanceUntilIdle()

            // A caller that opted out of the global indicator is exactly the one driving its own,
            // so onLoading must not be gated on manageLoading.
            assertTrue(viewModel.loadingSignals.isEmpty(), "global indicator untouched")
            assertEquals(listOf(true, false), viewModel.reportedLoading, "caller still told")
        }

    @Test
    fun tells_the_caller_about_loading_when_it_does_own_the_indicator() = runTest(dispatcher) {
        val viewModel = TestVM()

        viewModel.runTwo(useCaseFlow(1), useCaseFlow("a"))
        advanceUntilIdle()

        assertEquals(listOf(true, false), viewModel.reportedLoading)
    }

    // --- the positional contract ---------------------------------------------------------------

    @Test
    fun hands_results_back_in_flow_order() = runTest(dispatcher) {
        val viewModel = TestVM()
        var results: List<Any?> = emptyList()

        viewModel.runMany(useCaseFlow(1), useCaseFlow("a"), useCaseFlow(true)) { results = it }
        advanceUntilIdle()

        assertEquals(listOf<Any?>(1, "a", true), results)
    }

    @Test
    fun leaves_a_gap_in_place_for_the_flow_that_failed() = runTest(dispatcher) {
        val viewModel = TestVM()
        val boom: LeonException = LeonException.Unknown("boom")
        var first: Int? = null
        var second: String? = null
        var firstException: LeonException? = null
        var secondException: LeonException? = null

        viewModel.runTwo(
            first = failingFlow(boom),
            second = useCaseFlow("a"),
            onSuccess = { a, b -> first = a; second = b },
            onFailure = { a, b -> firstException = a; secondException = b }
        )
        advanceUntilIdle()

        // The failure must not shift the second flow's result into the first slot.
        assertNull(first, "the failed flow's slot is null, not missing")
        assertEquals("a", second)
        assertEquals(boom, firstException)
        assertNull(secondException)
    }

    // --- failure routing -----------------------------------------------------------------------

    @Test
    fun sends_the_same_failure_to_handleException_only_once() = runTest(dispatcher) {
        val viewModel = TestVM()
        val boom: LeonException = LeonException.Unknown("boom")

        viewModel.runMany(failingFlow<Int>(boom), failingFlow<Int>(boom), useCaseFlow(1))
        advanceUntilIdle()

        // Three calls dying on one outage should not queue three identical snackbars.
        assertEquals(listOf(boom), viewModel.handled)
    }

    @Test
    fun sends_each_distinct_failure_to_handleException() = runTest(dispatcher) {
        val viewModel = TestVM()
        val first: LeonException = LeonException.Unknown("first")
        val second: LeonException = LeonException.Unknown("second")

        viewModel.runMany(failingFlow<Int>(first), failingFlow<Int>(second))
        advanceUntilIdle()

        assertEquals(listOf(first, second), viewModel.handled)
    }
}
