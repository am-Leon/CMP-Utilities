package am.leon.utilities.common.domain.interactor

import am.leon.utilities.common.data.models.Resource
import am.leon.utilities.common.data.models.exception.LeonException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Pins the emission sequence of [UseCase], because the loading indicator is driven off it: the
 * global counter can only stay balanced if each call signals "on" and "off" exactly once.
 */
class UseCaseTest {

    private class TestUC(private val result: suspend () -> Int) : UseCase<Int, Unit>() {
        override fun invoke(body: Unit?, multipleInvoke: Boolean): Flow<Resource<Int>> =
            executeRemote(multipleInvoke = multipleInvoke, remoteExecution = { result.invoke() })
    }

    @Test
    fun brackets_the_result_with_one_progress_on_and_one_progress_off() = runTest {
        val emissions = TestUC { 7 }.invoke(multipleInvoke = false).toList()

        assertEquals(3, emissions.size)
        assertEquals(true, (emissions[0] as Resource.Progress).loading)
        assertEquals(7, (emissions[1] as Resource.Success).model)
        assertEquals(false, (emissions[2] as Resource.Progress).loading)
    }

    @Test
    fun emits_no_progress_at_all_when_someone_else_owns_the_indicator() = runTest {
        val emissions = TestUC { 7 }.invoke(multipleInvoke = true).toList()

        assertEquals(1, emissions.size)
        assertEquals(7, (emissions[0] as Resource.Success).model)
    }

    @Test
    fun still_signals_progress_off_on_failure_even_when_someone_else_owns_the_indicator() =
        runTest {
            val emissions = TestUC { throw LeonException.Unknown(message = "boom") }
                .invoke(multipleInvoke = true).toList()

            // Asymmetric with the success path above, which emits no Progress at all. Harmless
            // while the indicator is a Boolean; a second "off" per call unbalances a counter.
            assertEquals(2, emissions.size)
            assertIs<Resource.Failure>(emissions[0])
            assertEquals(false, (emissions[1] as Resource.Progress).loading)
        }
}
