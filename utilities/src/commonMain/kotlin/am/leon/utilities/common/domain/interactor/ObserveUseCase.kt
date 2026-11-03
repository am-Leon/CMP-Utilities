package am.leon.utilities.common.domain.interactor

import am.leon.utilities.common.data.models.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Contract for a use case that tracks a source instead of running once: it emits every time the
 * source changes and never completes, for long-lived UI such as a top bar avatar that has to
 * follow the cache for as long as the screen is alive.
 *
 * It is a sibling of [UseCase], not a subclass, and the differences are deliberate:
 *
 * - **No loading phase.** A background subscription has no business driving the global indicator,
 *   so there is no `multipleInvoke` to switch one off.
 * - **Failure does not end the stream.** [observe] reports the failure downstream and then
 *   resubscribes. `catch` would emit once and complete, leaving the UI frozen on stale data with
 *   nothing to say so and nothing to restart it.
 * - **No `executeLocal` / `executeRemote`.** Those run their block exactly once.
 *
 * The source itself is expected never to complete. One that does end the observation, because
 * only a failure is retried - that is a bug in the repository, not something to paper over here.
 *
 * @param Domain the model emitted to the view on every change.
 * @param Body parameters for the observation, when it needs any.
 * @param dispatcher where the source is collected. It is a constructor parameter, not a hardcoded
 * [Dispatchers.IO], so a test can hand in its own scheduler and keep virtual time in control.
 */
abstract class ObserveUseCase<out Domain, in Body>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    abstract operator fun invoke(body: Body? = null): Observation<Domain>

    /**
     * The same observation as [invoke], as a plain [Flow].
     *
     * For Swift. [Observation] is a value class, and the Obj-C bridge erases it: `invoke` exports
     * as returning an untyped `id`, and the class itself is not exported at all, so there is
     * nothing on the Swift side to unwrap it with. This hands back the typed `Flow` instead, which
     * exports the way [UseCase.invoke] does. Kotlin callers should stay on [invoke].
     */
    fun observeAsFlow(body: Body? = null): Flow<Resource<Domain>> = invoke(body).flow

    //----------------------------------------------------------------------------------------------

    /**
     * Wraps [source] as an [Observation]: maps each value to [Resource.Success], and on failure
     * emits [Resource.Failure] and resubscribes.
     *
     * Retrying is unbounded: the usual source is a cache whose read can fail for a reason the next
     * write clears, and giving up would leave a permanently dead stream. The wait doubles from
     * [retryDelay] to [maxRetryDelay], so a source broken for good settles into a rare retry rather
     * than emitting failures several times a second. It counts every failure since collection
     * started, not consecutive ones.
     *
     * [Domain] is redeclared because the class declares it `out` and `source` is an `in` position.
     */
    protected fun <Domain> observe(
        retryDelay: Duration = DEFAULT_RETRY_DELAY,
        maxRetryDelay: Duration = DEFAULT_MAX_RETRY_DELAY,
        source: () -> Flow<Domain>
    ): Observation<Domain> = Observation(
        source.invoke()
            .map<Domain, Resource<Domain>> { Resource.success(it) }
            .retryWhen { cause, attempt ->
                emit(Resource.failure(cause.asLeonException(action = "observing")))
                delay(backoff(attempt = attempt, base = retryDelay, max = maxRetryDelay))
                true
            }
            .flowOn(dispatcher)
    )

    private fun backoff(attempt: Long, base: Duration, max: Duration): Duration {
        val factor = 1 shl attempt.coerceAtMost(MAX_BACKOFF_SHIFT).toInt()
        return (base * factor).coerceAtMost(max)
    }

    private companion object {
        val DEFAULT_RETRY_DELAY: Duration = 2.seconds
        val DEFAULT_MAX_RETRY_DELAY: Duration = 1.minutes

        // Caps the doubling before it can overflow the multiplication.
        const val MAX_BACKOFF_SHIFT: Long = 16
    }
}
