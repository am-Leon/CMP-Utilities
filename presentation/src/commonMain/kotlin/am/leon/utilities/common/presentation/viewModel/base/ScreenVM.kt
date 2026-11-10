package am.leon.utilities.common.presentation.viewModel.base

import am.leon.navigation.api.IDestination
import am.leon.navigation.api.INavigator
import am.leon.navigation.api.NavigationResultKey
import am.leon.navigation.api.ResultAction
import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.common.presentation.appEvents.AppEvent
import am.leon.utilities.common.presentation.appEvents.controller.IAppEventController
import am.leon.utilities.common.presentation.appEvents.state.IMessageEvent
import am.leon.utilities.common.presentation.appEvents.state.MessageAction
import am.leon.utilities.common.presentation.appEvents.state.MessageDuration
import am.leon.utilities.common.presentation.appEvents.state.MessageType
import am.leon.utilities.common.presentation.viewModel.contract.IAction
import am.leon.utilities.common.presentation.viewModel.contract.IEvent
import am.leon.utilities.common.presentation.viewModel.contract.IState
import am.leon.utilities.common.presentation.viewModel.exception.ExceptionHandler
import am.leon.utilities.common.presentation.viewModel.exception.IExceptionHandler
import am.leon.utilities.common.presentation.viewModel.exception.IExceptionHandlerEvent
import am.leon.utilities.common.presentation.viewModel.exception.IExceptionMapper
import am.leon.utilities.extentions.IString
import am.leon.utilities.helpers.inputField.DynamicInputName
import am.leon.utilities.helpers.inputField.IInputFieldError
import am.leon.utilities.helpers.inputField.IInputFieldKey
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * A ViewModel bound to one screen, and the only base an app extends.
 *
 * On top of the state machine ([MviVM]) and the use-case operators ([InteractorVM]) it adds
 * everything that ties a screen to the surrounding app:
 * - the [destination] it belongs to, with [onActive]/[onInactive] as it comes and goes and
 *   [onRefresh] for a pull-to-refresh aimed at it;
 * - the global indicator and the snackbar, through an [IAppEventController];
 * - the [navigator], and results handed back from another screen;
 * - how a [LeonException] turns into something the user sees, via [createExceptionHandler].
 *
 * Everything in here needs a running app to exercise, which is why the two layers above it do not
 * live in this class.
 *
 * @param destination the screen this ViewModel belongs to, used to tell whether it is the one in
 * front.
 * @param vmCore the app-wide dependencies, as one definition. An app with more of its own
 * passes an [IVMCore] that carries them; see that interface.
 */
abstract class ScreenVM<Action : IAction, Event : IEvent, State : IState, Destination : IDestination>(
    initialState: State, protected val destination: Destination, vmCore: IVMCore
) : InteractorVM<Action, Event, State>(initialState) {

    protected val navigator: INavigator = vmCore.navigator

    /**
     * The app-wide event bus. Protected so an app can follow its own
     * [am.leon.utilities.common.presentation.appEvents.IAppEvent]s from a screen,
     * the way [AppEvent] is followed in this class's `init`:
     *
     * ```
     * init {
     *     appEventController.events
     *         .filterIsInstance<ShopEvent.CartUpdated>()
     *         .onEach { ... }
     *         .launchIn(viewModelScope)
     * }
     * ```
     *
     * Subscribe from `init`, not from [onInit] or [onActive]: the stream has no replay, so a
     * collector that starts later sees nothing that was emitted before it.
     */
    protected val appEventController: IAppEventController = vmCore.appEventController
    private val exceptionHandler: IExceptionHandler by lazy { createExceptionHandler(::dispatchExceptionEvent) }

    /**
     * The field keys this screen shows errors on itself, through [bindFieldErrors], so they are
     * not repeated in whatever [presentExceptionEvent] does with the rest.
     */
    protected open val boundFieldKeys: Set<IInputFieldKey> = emptySet()

    private var isRefreshingAction = false

    /** How many of the controller's loading count this VM is holding, so it gives back no more. */
    private var ownedLoading = 0

    /**
     * How many calls this VM has routed to the refresh spinner instead of the blocking indicator.
     *
     * Counted rather than flagged because [isRefreshingAction] alone cannot tell the refresh's own
     * call apart from one that was already running when the user pulled: whichever finished first
     * would take the other's place, stranding the indicator or hiding it early.
     */
    private var refreshingOwned = 0

    protected val isCurrentDestination: Boolean
        get() = appEventController.currentDestination.value == destination

    // ---------------------------------------------------------------------------------------------

    init {
        // A global refresh reaches only the screen in front.
        appEventController.events
            .filter { it is AppEvent.RefreshRequested && isCurrentDestination }
            .onEach {
                isRefreshingAction = true
                onRefresh()
            }
            .launchIn(viewModelScope)

        // onActive / onInactive follow this destination arriving in front and leaving it.
        appEventController.currentDestination
            .onEach { if (it == destination) onActive() else onInactive() }
            .launchIn(viewModelScope)
    }

    /** This destination came to the front: resume polling, install a top-bar override. */
    protected open fun onActive() {}

    /** The app navigated away: pause background work, drop transient UI state. */
    protected open fun onInactive() {}

    /**
     * Pull-to-refresh, with the refreshing state already set. Override for a refresh that differs
     * from a first load - cache-bypassing calls, resetting pagination.
     */
    protected open fun onRefresh() = onInit()

    final override fun handleException(exception: LeonException) =
        exceptionHandler.handleException(exception)

    /**
     * The handler that decides what each [LeonException] does to the UI.
     *
     * Override to install an app's own mapping: an [IExceptionMapper] that claims the cases the
     * app knows about and returns `null` for the rest, which fall through to the library's own
     * mapping. Pass `onUnknownInputError` as well when the app has [IInputFieldError] types of
     * its own.
     *
     * ```
     * override fun createExceptionHandler(onEvent: (IExceptionHandlerEvent) -> Unit) =
     *     ExceptionHandler(handler = onEvent) { exception ->
     *         ShopExceptionEvent.PaymentRequired.takeIf {
     *             exception is LeonException.Client.Unhandled && exception.httpErrorCode == 402
     *         }
     *     }
     * ```
     */
    protected open fun createExceptionHandler(onEvent: (IExceptionHandlerEvent) -> Unit): IExceptionHandler =
        ExceptionHandler(handler = onEvent)

    /**
     * Routes what the mapper decided. It acts on none of it: everything a user would see is the
     * app's to choose, in [presentExceptionEvent].
     */
    private fun dispatchExceptionEvent(event: IExceptionHandlerEvent) {
        when (event) {
            // Raised as a fact, never presented here: one reaction per app, in its
            // AppEvent.SessionExpired collector, not one per screen that got the 401.
            is IExceptionHandlerEvent.AuthorizationRequired ->
                appEventController.tryEmit(AppEvent.SessionExpired)

            is IExceptionHandlerEvent.HandleValidationErrors -> {
                bindFieldErrors(event.errors)
                event.errors.filterKeys(::isUnbound).takeIf { it.isNotEmpty() }?.let {
                    presentExceptionEvent(IExceptionHandlerEvent.HandleValidationErrors(it))
                }
            }

            // ShowMessage, and whatever an app's own mapper returned.
            else -> presentExceptionEvent(event)
        }
    }

    /**
     * Shows the user what went wrong - wording, surface and duration all the app's.
     *
     * Abstract rather than defaulted to nothing: it is the only place an error becomes visible, so
     * a ViewModel that forgot it would swallow every failure in silence.
     *
     * Receives [IExceptionHandlerEvent.ShowMessage], whatever an app's [IExceptionMapper] returned,
     * and [IExceptionHandlerEvent.HandleValidationErrors] narrowed to the fields [bindFieldErrors]
     * did not take - never [IExceptionHandlerEvent.AuthorizationRequired].
     */
    protected abstract fun presentExceptionEvent(event: IExceptionHandlerEvent)

    /**
     * Puts each validation error on the field that owns it, typically `emit { copy(...) }`. Only
     * the keys this screen claims reach a field - [boundFieldKeys], or [bindsDynamicField] for the
     * ones the backend names at runtime; everything else goes to [presentExceptionEvent].
     */
    protected open fun bindFieldErrors(errors: Map<IInputFieldKey, IString>) = Unit

    /**
     * Claims a [DynamicInputName] - a key the backend named at runtime, like `items.0`, which no
     * declared [boundFieldKeys] entry can match - as one [bindFieldErrors] puts on a field.
     * Anything left unclaimed still reaches [presentExceptionEvent] rather than being dropped.
     */
    protected open fun bindsDynamicField(key: DynamicInputName): Boolean = false

    private fun isUnbound(key: IInputFieldKey): Boolean =
        key !in boundFieldKeys && (key !is DynamicInputName || bindsDynamicField(key).not())

    /**
     * What a retry does: replay the last action, or [onInit] when there was none. Wire it into
     * whatever [presentExceptionEvent] shows for a [IExceptionHandlerEvent.ShowMessage] that
     * carries `withRetryAction`.
     */

    protected open fun onRetryAction() = lastAction.value?.let(::processIntent) ?: onInit()

    // ---------------------------------------------------------------------------------------------

    /**
     * Helper to handle navigation results from another screen.
     *
     * @param key The unique key for the result.
     * @param onRefresh Callback when a generic refresh is requested.
     * @param onUpdated Callback when an item is updated.
     * @param onCreated Callback when a new item is created.
     * @param onDeleted Callback when an item is deleted.
     */
    protected fun <T : Any> INavigator.handleNavigationResult(
        key: NavigationResultKey<ResultAction<T>>,
        onRefresh: () -> Unit = { onInit() },
        onUpdated: (T) -> Unit = {},
        onCreated: (T) -> Unit = {},
        onDeleted: (T) -> Unit = {}
    ) {
        launchOnMain {
            observeResult(key).collectLatest { action ->
                when (action) {
                    is ResultAction.Updated -> onUpdated(action.data)
                    is ResultAction.Refreshed -> onRefresh()
                    is ResultAction.Created -> onCreated(action.data)
                    is ResultAction.Deleted -> onDeleted(action.data)
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Adds or removes this ViewModel's share of the global indicator - or of the refresh spinner,
     * while a pull-to-refresh is in flight.
     *
     * Turning it **on** is skipped unless this is the screen in front, so background work cannot
     * spin over it. Turning it **off** is not, or a ViewModel that navigated away would strand the
     * indicator - but it gives back only what it took ([ownedLoading]), since the controller counts
     * callers and an unmatched "off" would clear the count another screen is holding.
     *
     * While a pull-to-refresh is pending, calls started by it are counted separately
     * ([refreshingOwned]) and drive the refresh spinner instead. Both counts are symmetric, so
     * whichever call finishes first, each indicator is released exactly once.
     */
    final override fun emit(isLoading: Boolean) {
        if (isLoading && !isCurrentDestination) return

        if (isLoading) {
            // While a pull-to-refresh is pending, the call it started drives the refresh spinner;
            // anything else still drives the blocking indicator.
            if (isRefreshingAction) {
                refreshingOwned++
                appEventController.setRefreshing(true)
            } else {
                ownedLoading++
                appEventController.setLoading(isLoading = true)
            }
            return
        }

        // The refresh branch gives back exactly as many "off"s as it took "on"s, so a call that
        // was already running when the user pulled can no longer be mistaken for the refresh's
        // own - and the spinner stays up until every diverted call has finished, not just the
        // first one.
        if (refreshingOwned > 0) {
            refreshingOwned--
            if (refreshingOwned == 0) {
                appEventController.setRefreshing(false)
                isRefreshingAction = false
            }
            return
        }

        // A refresh whose onRefresh() started no managed call of its own ends here, rather than
        // leaving the flag set to divert the next ordinary load into the pull spinner.
        if (isRefreshingAction) {
            isRefreshingAction = false
            appEventController.setRefreshing(false)
        }

        // Give back only what this VM took. The matching "on" is skipped for a VM that is not
        // the current destination, and decrementing anyway would clear a count that the
        // screen in front is still holding.
        if (ownedLoading == 0) return
        ownedLoading--
        appEventController.setLoading(isLoading = false)
    }

    /**
     * Shows a snackbar, closing any open bottom sheet first.
     *
     * That dismissal is not a convenience and is deliberately not optional: a snackbar is hosted in
     * the screen's own content, below the sheet and its scrim, so raising one while a sheet is open
     * would show the user nothing at all. A message that must not disturb the sheet belongs in
     * [emitDialog], which renders above it.
     */
    protected fun emit(
        message: IString, isDismissible: Boolean = true, action: MessageAction? = null,
        messageType: MessageType = MessageType.DEFAULT,
        duration: MessageDuration = MessageDuration.SHORT
    ) {
        appEventController.tryEmit(AppEvent.SheetDismiss)
        appEventController.tryEmit(
            AppEvent.MessageEvent(
                IMessageEvent.Snackbar(
                    message = message, action = action, type = messageType, duration = duration,
                    isDismissible = isDismissible
                )
            )
        )
    }

    /**
     * Shows a dialog, leaving any open bottom sheet where it is.
     *
     * The opposite of [emit] on purpose: a dialog renders above the sheet, so there is nothing to
     * get out of its way, and a confirmation raised from inside a sheet should hand the user back
     * to that sheet once it is answered.
     */
    protected fun emitDialog(
        message: IString, title: IString? = null,
        messageType: MessageType = MessageType.DEFAULT,
        confirmAction: MessageAction? = null, dismissAction: MessageAction? = null
    ) {
        appEventController.tryEmit(
            AppEvent.MessageEvent(
                IMessageEvent.Dialog(
                    title = title, message = message, type = messageType,
                    confirmAction = confirmAction, dismissAction = dismissAction
                )
            )
        )
    }
}