package am.leon.utilities.common.presentation.viewModel.base

import am.leon.utilities.common.data.models.Resource
import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.common.domain.interactor.Observation
import am.leon.utilities.common.domain.interactor.UseCase
import am.leon.utilities.common.domain.models.request.IRequestValidation
import am.leon.utilities.common.presentation.viewModel.contract.IAction
import am.leon.utilities.common.presentation.viewModel.contract.IEvent
import am.leon.utilities.common.presentation.viewModel.contract.IState
import am.leon.utilities.extentions.isFailure
import am.leon.utilities.extentions.isProgress
import am.leon.utilities.extentions.isSuccess
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

/**
 * Runs use cases into the state machine above it: collects a [UseCase]'s [Resource] stream, routes
 * every failure to [handleException], and signals the loading indicator once on and once off.
 *
 * The indicator is the abstract [emit] here rather than an `IAppEventController`, which is what
 * lets these operators be tested against a list of booleans instead of a running app - the reason
 * this layer sits apart from [ScreenVM].
 *
 * Apps do not extend this directly; see [MviVM] on the internal constructor.
 */
abstract class InteractorVM<Action : IAction, Event : IEvent, State : IState> internal constructor(
    initialState: State
) : MviVM<Action, Event, State>(initialState) {

    /**
     * Signals the global loading indicator. Called once with `true` when a call starts and once
     * with `false` when it ends, so whatever counts callers on the other side stays balanced.
     */
    protected abstract fun emit(isLoading: Boolean)

    // ---------------------------------------------------------------------------------------------

    /**
     * Collects an [Observation] for as long as this ViewModel lives.
     *
     * Unlike [result], it never touches [emit]: a background subscription must not drive the global
     * indicator, and the `onCompletion` that would clear it never runs on a stream that does not
     * end. Failures skip [handleException] too - the stream retries itself, so pass [onFailure]
     * only when a screen has to react.
     *
     * @return the subscription's [Job], to stop following the source before [onCleared].
     */
    protected fun <Domain> Observation<Domain>.observe(
        onChange: (Domain) -> Unit, onFailure: ((LeonException) -> Unit)? = null
    ): Job = launchOnMain {
        flow.collect {
            when (it) {
                is Resource.Success -> onChange(it.model)
                is Resource.Failure -> onFailure?.invoke(it.exception)
                is Resource.Progress -> Unit // an observation has no loading phase
            }
        }
    }

    /**
     * @param manageLoading whether this call drives the global indicator. Pass `false` when
     * something else already owns it for this screen.
     */
    protected fun <Domain> Flow<Resource<Domain>>.result(
        onSuccess: (Domain) -> Unit,
        onFailure: ((LeonException) -> Unit)? = null,
        onLoading: ((Boolean) -> Unit)? = null,
        manageLoading: Boolean = true
    ) = launchOnMain {
        this@result
            // onStart and onCompletion always pair - on a throw inside the callbacks, and on
            // cancellation - so the controller's count cannot drift. The upstream's own trailing
            // Progress(false) is deliberately not forwarded: a second "off" for one "on" would
            // clear the indicator while other calls are still running.
            .onStart { if (manageLoading) emit(isLoading = true) }
            .onCompletion { if (manageLoading) emit(isLoading = false) }
            .collect {
                when (it) {
                    is Resource.Failure -> {
                        handleException(it.exception)
                        onFailure?.invoke(it.exception)
                    }

                    is Resource.Progress -> onLoading?.invoke(it.loading)

                    is Resource.Success -> onSuccess(it.model)
                }
            }
    }

    /**
     * Collects several [Resource] flows together and reports them positionally: entry `n` of
     * [onSuccess] is always the result of flow `n`, null when that flow failed, and entry `n` of
     * [onFailure] is its exception, null when it succeeded.
     *
     * @param manageLoading whether this call drives the global indicator. Pass `false` when
     * something else already owns it for this screen. [onLoading] fires either way: a caller that
     * opted out of the global indicator is precisely the one driving its own.
     */
    protected fun combine(
        vararg flows: Flow<Resource<Any?>>,
        onSuccess: (results: List<Any?>) -> Unit,
        onFailure: ((exceptions: List<LeonException?>) -> Unit)? = null,
        onLoading: ((isLoading: Boolean) -> Unit)? = null,
        manageLoading: Boolean = true,
    ) = launchOnMain {
        combine(
            flows = flows.map { flow -> flow.filterNot { it.isProgress() } },
            transform = { it.toList() }
        ).onStart {
            if (manageLoading) emit(isLoading = true)
            onLoading?.invoke(true)
        }.onCompletion {
            // Paired with onStart above, and outside collectLatest: the combined flow re-emits on
            // every upstream change, and clearing in there would decrement once per emission.
            if (manageLoading) emit(isLoading = false)
            onLoading?.invoke(false)
        }.collectLatest { resourceList ->
            val successResults = mutableListOf<Any?>()
            val failureExceptions = mutableListOf<LeonException?>()

            resourceList.forEach { resource ->
                when {
                    resource.isSuccess() -> {
                        successResults.add(resource.model)
                        failureExceptions.add(null)
                    }

                    resource.isFailure() -> {
                        successResults.add(null)
                        failureExceptions.add(resource.exception)
                    }

                    // Unreachable while Progress is filtered upstream, and here on purpose: the
                    // overloads below read these lists by index, so an entry that went into
                    // neither list would shift every later result into the wrong slot instead of
                    // failing. Both lists stay the same length as `flows` whatever arrives.
                    else -> {
                        successResults.add(null)
                        failureExceptions.add(null)
                    }
                }
            }

            onSuccess(successResults)
            onFailure?.invoke(failureExceptions)
            failureExceptions.asSequence().filterNotNull().toSet().forEach(::handleException)
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <FirstResult, SecondResult> combine(
        firstFlow: Flow<Resource<FirstResult>>,
        secondFlow: Flow<Resource<SecondResult>>,
        onSuccess: (firstResult: FirstResult?, secondResult: SecondResult?) -> Unit,
        onFailure: ((firstException: LeonException?, secondException: LeonException?) -> Unit)? = null,
        onLoading: ((isLoading: Boolean) -> Unit)? = null,
        manageLoading: Boolean = true,
    ) = combine(
        flows = arrayOf(firstFlow, secondFlow),
        onSuccess = { (firstResult, secondResult) ->
            // `as?` on an erased type parameter never fails and never yields null - it is here to
            // name the type, not to check it. Correctness rests on combine keeping flow order.
            onSuccess(
                firstResult as? FirstResult,
                secondResult as? SecondResult
            )
        },
        onFailure = { (firstException, secondException) ->
            onFailure?.invoke(firstException, secondException)
        },
        onLoading = onLoading,
        manageLoading = manageLoading
    )

    @Suppress("UNCHECKED_CAST")
    protected fun <FirstResult, SecondResult, ThirdResult> combine(
        firstFlow: Flow<Resource<FirstResult>>,
        secondFlow: Flow<Resource<SecondResult>>,
        thirdFlow: Flow<Resource<ThirdResult>>,
        onSuccess: (firstResult: FirstResult?, secondResult: SecondResult?, thirdResult: ThirdResult?) -> Unit,
        onFailure: ((firstException: LeonException?, secondException: LeonException?, thirdException: LeonException?) -> Unit)? = null,
        onLoading: ((isLoading: Boolean) -> Unit)? = null,
        manageLoading: Boolean = true,
    ) = combine(
        flows = arrayOf(firstFlow, secondFlow, thirdFlow),
        onSuccess = { (firstResult, secondResult, thirdResult) ->
            // `as?` on an erased type parameter never fails and never yields null - it is here to
            // name the type, not to check it. Correctness rests on combine keeping flow order.
            onSuccess(
                firstResult as? FirstResult,
                secondResult as? SecondResult,
                thirdResult as? ThirdResult
            )
        },
        onFailure = { (firstException, secondException, thirdException) ->
            onFailure?.invoke(firstException, secondException, thirdException)
        },
        onLoading = onLoading,
        manageLoading = manageLoading
    )

    @Suppress("UNCHECKED_CAST")
    protected fun <FirstResult, SecondResult, ThirdResult, FourthResult> combine(
        firstFlow: Flow<Resource<FirstResult>>,
        secondFlow: Flow<Resource<SecondResult>>,
        thirdFlow: Flow<Resource<ThirdResult>>,
        fourthFlow: Flow<Resource<FourthResult>>,
        onSuccess: (firstResult: FirstResult?, secondResult: SecondResult?, thirdResult: ThirdResult?, fourthResult: FourthResult?) -> Unit,
        onFailure: ((firstException: LeonException?, secondException: LeonException?, thirdException: LeonException?, fourthException: LeonException?) -> Unit)? = null,
        onLoading: ((isLoading: Boolean) -> Unit)? = null,
        manageLoading: Boolean = true,
    ) = combine(
        flows = arrayOf(firstFlow, secondFlow, thirdFlow, fourthFlow),
        onSuccess = { (firstResult, secondResult, thirdResult, fourthResult) ->
            // `as?` on an erased type parameter never fails and never yields null - it is here to
            // name the type, not to check it. Correctness rests on combine keeping flow order.
            onSuccess(
                firstResult as? FirstResult,
                secondResult as? SecondResult,
                thirdResult as? ThirdResult,
                fourthResult as? FourthResult
            )
        },
        onFailure = { (firstException, secondException, thirdException, fourthException) ->
            onFailure?.invoke(firstException, secondException, thirdException, fourthException)
        },
        onLoading = onLoading,
        manageLoading = manageLoading
    )

    protected fun <Model, Request : IRequestValidation<*>> UseCase<Model, Request>.invokeIf(
        condition: Boolean, body: Request, multipleInvoke: Boolean = false, fallback: Model? = null,
    ): Flow<Resource<Model?>> {
        return if (condition) invoke(body = body, multipleInvoke = multipleInvoke)
        else flowOf(Resource.Success(fallback))
    }
}