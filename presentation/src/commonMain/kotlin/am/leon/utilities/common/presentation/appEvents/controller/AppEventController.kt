package am.leon.utilities.common.presentation.appEvents.controller

import am.leon.navigation.api.IDestination
import am.leon.utilities.common.presentation.appEvents.AppEvent
import am.leon.utilities.common.presentation.appEvents.IAppEvent
import androidx.compose.runtime.Stable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet

/**
 * A centralized controller for managing global application events and unified UI states.
 *
 * This controller serves as the bridge between ViewModels and app-wide UI components
 * (like Snackbars, Dialogs, Loading Indicators, and Global Refresh gestures).
 */
@Stable
interface IAppEventController {

    /**
     * A stream of transient [IAppEvent]s - this library's [AppEvent]s and any an app raises.
     * These events represent one-time actions like showing a snackbar or session expiration.
     * They are consumed once and not re-emitted on configuration changes.
     */
    val events: Flow<IAppEvent>

    /**
     * Indicates whether a global full-screen loading operation is in progress.
     * Usually triggers a non-dismissible loading dialog.
     *
     * It counts callers rather than holding a flag, so it stays on until the last concurrent
     * operation reports back instead of being switched off by whichever one finishes first.
     */
    val isLoading: StateFlow<Boolean>

    /**
     * Indicates whether a pull-to-refresh operation is in progress.
     * Controls the visibility of the native refresh indicator.
     */
    val isRefreshing: StateFlow<Boolean>

    /**
     * Holds the [IDestination] of the screen currently visible in the foreground.
     * Used by ViewModels to coordinate active-only behaviors (e.g., reacting to refresh only if visible).
     */
    val currentDestination: StateFlow<IDestination?>

    /**
     * Suspends and emits a transient [IAppEvent].
     */
    suspend fun emit(event: IAppEvent)

    /**
     * Attempts to emit a transient [IAppEvent] without suspending.
     * @return true if the event was successfully queued.
     */
    fun tryEmit(event: IAppEvent): Boolean

    /**
     * Adds (`true`) or removes (`false`) one caller from the [isLoading] count.
     *
     * Calls must be balanced: whoever turns it on is the one that turns it off. To clear the
     * indicator outright, regardless of who is still counted, use [resetLoading].
     */
    fun setLoading(isLoading: Boolean)

    /**
     * Drops the [isLoading] count to zero and hides the indicator, whatever is still outstanding.
     * For recovery paths - a session reset, a hard navigation - not for ending an operation.
     */
    fun resetLoading()

    /**
     * Updates the global [isRefreshing] state.
     */
    fun setRefreshing(isRefreshing: Boolean)

    /**
     * Updates the [currentDestination] whenever the app's backstack changes.
     * This should be called by the navigation host whenever the top destination changes.
     */
    fun updateDestination(destination: IDestination?)

    /**
     * Triggers a global refresh request ([AppEvent.RefreshRequested]).
     * Usually called by the top-level PullToRefresh UI component.
     */
    fun triggerRefresh()
}

/**
 * Creates the default [IAppEventController].
 *
 * A factory rather than a public class: the implementation stays `internal` so it can change
 * without breaking consumers, while DI can still do `single<IAppEventController> { AppEventController() }`.
 */
fun AppEventController(): IAppEventController = DefaultAppEventController()

internal class DefaultAppEventController : IAppEventController {
    private val _events = MutableSharedFlow<IAppEvent>(
        replay = 0,
        extraBufferCapacity = 64, // Prevents emission suspension if a collector is briefly slow,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val events: Flow<IAppEvent> = _events.asSharedFlow()

    private val loadingCount = MutableStateFlow(0)
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    override val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _currentDestination = MutableStateFlow<IDestination?>(null)
    override val currentDestination: StateFlow<IDestination?> = _currentDestination.asStateFlow()

    override suspend fun emit(event: IAppEvent) {
        _events.emit(event)
    }

    override fun tryEmit(event: IAppEvent): Boolean {
        return _events.tryEmit(event)
    }

    override fun setLoading(isLoading: Boolean) {
        val count = loadingCount.updateAndGet { current ->
            if (isLoading) current + 1 else (current - 1).coerceAtLeast(0)
        }
        _isLoading.value = count > 0
    }

    override fun resetLoading() {
        loadingCount.value = 0
        _isLoading.value = false
    }

    override fun setRefreshing(isRefreshing: Boolean) {
        _isRefreshing.value = isRefreshing
    }

    override fun updateDestination(destination: IDestination?) {
        _currentDestination.value = destination
    }

    override fun triggerRefresh() {
        tryEmit(AppEvent.RefreshRequested)
    }
}