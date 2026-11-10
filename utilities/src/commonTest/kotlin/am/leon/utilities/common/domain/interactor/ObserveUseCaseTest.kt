package am.leon.utilities.common.domain.interactor

import am.leon.utilities.common.data.models.Resource
import am.leon.utilities.common.data.models.exception.LeonException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveUseCaseTest {

    private class TestObserveUC(
        dispatcher: CoroutineDispatcher, private val source: () -> Flow<Int>
    ) : ObserveUseCase<Int, Unit>(dispatcher) {
        override fun invoke(body: Unit?): Observation<Int> =
            observe(retryDelay = 1.seconds, maxRetryDelay = 4.seconds) { source.invoke() }
    }

    @Test
    fun maps_every_source_value_to_a_success() = runTest {
        val useCase = TestObserveUC(StandardTestDispatcher(testScheduler)) {
            flow {
                emit(1)
                emit(2)
                awaitCancellation()
            }
        }

        val received = mutableListOf<Resource<Int>>()
        val job = launch { useCase().flow.collect { received.add(it) } }
        advanceUntilIdle()

        assertEquals(listOf(1, 2), received.map { (it as Resource.Success).model })
        job.cancel()
    }

    @Test
    fun reports_a_failure_and_keeps_observing_instead_of_completing() = runTest {
        var subscriptions = 0
        val useCase = TestObserveUC(StandardTestDispatcher(testScheduler)) {
            flow {
                subscriptions++
                if (subscriptions == 1) throw LeonException.Unknown(message = "cache read failed")
                emit(42)
                awaitCancellation()
            }
        }

        val received = mutableListOf<Resource<Int>>()
        val job = launch { useCase().flow.collect { received.add(it) } }
        advanceUntilIdle()

        // The failure reaches the collector...
        assertIs<Resource.Failure>(received.first())
        // ...and the source is resubscribed rather than left for dead, which is what `catch` did.
        assertEquals(2, subscriptions)
        assertEquals(42, (received[1] as Resource.Success).model)
        // Still live: an observation outlives its errors.
        assertTrue(job.isActive)
        job.cancel()
    }

    @Test
    fun backs_off_so_a_source_that_stays_broken_does_not_spin() = runTest {
        val useCase = TestObserveUC(StandardTestDispatcher(testScheduler)) {
            flow { throw LeonException.Unknown(message = "storage is gone") }
        }

        val received = useCase().flow.take(4).toList()

        assertTrue(received.all { it is Resource.Failure })
        // 1s after the first failure, 2s after the second, then the 4s cap - not four in a row at
        // the base delay, which is what an un-backed-off retry would do.
        assertEquals(1.seconds + 2.seconds + 4.seconds, testScheduler.currentTime.milliseconds)
    }

    @Test
    fun wraps_a_non_leon_error_without_losing_it() = runTest {
        val useCase = TestObserveUC(StandardTestDispatcher(testScheduler)) {
            flow { throw IllegalStateException("storage is gone") }
        }

        val exception = (useCase().flow.take(1).toList().first() as Resource.Failure).exception

        assertIs<LeonException.Unknown>(exception)
        assertTrue(exception.message.orEmpty().contains("storage is gone"))
    }
}
