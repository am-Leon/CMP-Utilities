package am.leon.utilities.common.presentation.viewModel.base

import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.common.presentation.viewModel.contract.IAction
import am.leon.utilities.common.presentation.viewModel.contract.IEvent
import am.leon.utilities.common.presentation.viewModel.contract.IState
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * The MVI loop, and nothing else: [IAction]s in, one [IState] and a stream of [IEvent]s out, on a
 * scope that outlives configuration changes.
 *
 * It knows nothing about use cases, navigation or the app's chrome - that is the point. A subclass
 * of this layer can be built in a test with no fakes at all, which is what keeps the state machine
 * itself cheap to cover.
 *
 * Apps do not extend this directly: the constructor is internal, so [ScreenVM] stays the single
 * entry point. Drop the `internal` if a ViewModel ever genuinely has no screen behind it.
 *
 * @param Action top type of the actions this ViewModel subscribes to.
 * @param Event top type of the one-off events it emits.
 * @param State top type of the state it renders into.
 */
abstract class MviVM<Action : IAction, Event : IEvent, State : IState> internal constructor(
    initialState: State
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler(::onCoroutineException)

    private var isInitialized = false

    protected val oldViewState: State
        get() = _viewState.value

    protected open fun onInit() {}

    fun initialize() {
        if (!isInitialized) {
            onInit()
            isInitialized = true
        }
    }

    // ---------------------------------------------------------------------------------------------

    private val _viewState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val viewState: StateFlow<State> = _viewState.asStateFlow()

    /** A Channel, not a SharedFlow: an event is delivered once and never replayed on recreation. */
    private val _viewEvent = Channel<Event>(Channel.BUFFERED)
    val viewEvent: Flow<Event> = _viewEvent.receiveAsFlow()

    /** No replay, so an action sent before `init` subscribes is dropped. */
    private val _actionMutableFlow = MutableSharedFlow<Action>(extraBufferCapacity = Int.MAX_VALUE)

    /** What a retry replays; see `ScreenVM.onRetryAction`. */
    private val _lastAction = MutableStateFlow<Action?>(null)
    protected val lastAction: StateFlow<Action?> = _lastAction

    // ---------------------------------------------------------------------------------------------

    private fun onCoroutineException(context: CoroutineContext, throwable: Throwable) {
        if (throwable is LeonException) handleException(throwable)
        else handleException(LeonException.Unknown(throwable.message))
    }

    protected abstract fun handleException(exception: LeonException)

    /** Replaces the whole state - a first load or a reset. Partial updates go through [emit]. */
    protected fun resetState(newState: State) {
        _viewState.value = newState
    }

    /** Updates the state atomically, against the latest: `emit { copy(isLoading = true) }`. */
    protected fun emit(stateUpdater: State.() -> State) {
        _viewState.update { it.stateUpdater() }
    }

    protected fun emit(event: Event) {
        launchOnMain { _viewEvent.send(event) }
    }

    /**
     * Call this from the main dispatcher. From anywhere else, wrap it:
     * `withContext(Dispatchers.Main.immediate) { processIntent(action) }`.
     */
    fun processIntent(action: Action) {
        check(_actionMutableFlow.tryEmit(action)) { "Failed to emit action: $action" }
    }

    protected abstract fun clearState()

    protected abstract fun onActionTrigger(action: Action)

    init {
        launchOnMain {
            _actionMutableFlow.collect {
                _lastAction.value = it
                onActionTrigger(it)
            }
        }
    }

    @CallSuper
    override fun onCleared() {
        clearState()
        _viewEvent.close()
        super.onCleared()
    }

    // ---------------------------------------------------------------------------------------------

    protected fun launchOnMain(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(exceptionHandler, block = block)
    }

    protected fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(exceptionHandler + Dispatchers.IO, block = block)
    }

    protected fun launchOnDefault(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(exceptionHandler + Dispatchers.Default, block = block)
    }
}