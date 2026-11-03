package am.leon.utilities.common.presentation.viewModel

import am.leon.navigation.api.IDestination
import am.leon.navigation.compose.LocalDestination
import am.leon.utilities.common.presentation.appEvents.observers.ObserveAsEvents
import am.leon.utilities.common.presentation.viewModel.base.MviVM
import am.leon.utilities.common.presentation.viewModel.base.ScreenVM
import am.leon.utilities.common.presentation.viewModel.contract.IAction
import am.leon.utilities.common.presentation.viewModel.contract.IEvent
import am.leon.utilities.common.presentation.viewModel.contract.IState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Resolves this screen's [ScreenVM] from Koin, initializes it once, and renders [content] against
 * its state.
 *
 * ```
 * @Composable
 * internal fun LanguagesScreen() = VMProvider<LanguagesVM, LanguagesContract.State> { state -> }
 * ```
 *
 * Takes the screen's own state and nothing else. Reach for one of the overloads below as soon as
 * it has actions to send or events to receive.
 */
@Composable
inline fun <reified VM : ScreenVM<out IAction, out IEvent, State, out IDestination>, State : IState> VMProvider(
    noinline content: @Composable (state: State) -> Unit
) = ScreenState(screenVM<VM>().viewState, content)

/**
 * The same, plus the channel back: `onAction` is [MviVM.processIntent].
 *
 * ```
 * @Composable
 * internal fun LoginScreen() =
 *     VMProvider<LoginVM, LoginContract.Action, LoginContract.State> { state, onAction -> }
 * ```
 *
 * `Action` is invariant here and has to be: the screen *sends* actions, so the lambda it is handed
 * must take exactly the type this ViewModel reads.
 */
@Composable
inline fun <reified VM : ScreenVM<Action, out IEvent, State, out IDestination>, Action : IAction, State : IState> VMProvider(
    noinline content: @Composable (state: State, onAction: (Action) -> Unit) -> Unit
) {
    val vm = screenVM<VM>()
    ScreenState(vm.viewState) { content(it, vm::processIntent) }
}

/**
 * All three sides of the contract: state in, actions out, and the one-off [IEvent]s a screen acts
 * on once rather than renders - a dialog to open, a field to focus, a file to share.
 *
 * ```
 * @Composable
 * internal fun CartScreen() =
 *     VMProvider<CartVM, CartContract.Action, CartContract.Event, CartContract.State>(
 *         onEvent = { event -> when (event) { ... } }
 *     ) { state, onAction -> }
 * ```
 *
 * [onEvent] is collected through [ObserveAsEvents], so it is suspending, runs on
 * `Dispatchers.Main.immediate`, and only while the screen is at least `STARTED`. The underlying
 * channel buffers, so an event raised while the screen is in the background is delivered when it
 * comes back rather than dropped.
 *
 * The price of this overload is the fourth type argument, and `Event` being invariant: the screen
 * consumes events, so the lambda has to take exactly what this ViewModel sends. The two overloads
 * above project it `out` instead, which is why a ViewModel with an `Event` type of its own can
 * still use them - it simply does not observe.
 */
@Composable
inline fun <reified VM : ScreenVM<Action, Event, State, out IDestination>, Action : IAction, Event : IEvent, State : IState> VMProvider(
    noinline onEvent: suspend (event: Event) -> Unit,
    noinline content: @Composable (state: State, onAction: (Action) -> Unit) -> Unit
) {
    val vm = screenVM<VM>()
    ObserveAsEvents(flow = vm.viewEvent, onEvent = onEvent)
    ScreenState(vm.viewState) { content(it, vm::processIntent) }
}

// -------------------------------------------------------------------------------------------------

/**
 * Resolves the ViewModel for the destination this composable is hosted in and runs its [onInit]
 * once.
 *
 * `inline` only to carry `reified VM` as far as [koinViewModel]; [MviVM] rather than [ScreenVM] as
 * the bound so the overloads above can each keep their own, tighter one.
 *
 * @throws IllegalStateException when there is no destination in the hierarchy. Loud on purpose: a
 * screen outside the NavigationHost has no ViewModel to resolve and no scope to live in, and the
 * alternative to failing here is a null working its way into Koin's parameters.
 */
@PublishedApi
@Composable
internal inline fun <reified VM : MviVM<*, *, *>> screenVM(): VM {
    val destination = LocalDestination.current
        ?: error("VMProvider failed: LocalDestination is null. Ensure this Screen is hosted inside NavigationHost.")

    val vm = koinViewModel<VM>(parameters = { parametersOf(destination) })

    // initialize() guards itself, so re-entering composition with the same instance is a no-op.
    LaunchedEffect(vm) { vm.initialize() }
    return vm
}

/**
 * Collects [viewState] and hands each value to [content].
 *
 * Deliberately not inline, unlike everything above it: an inline composable has no restart group of
 * its own, so reading the state there would invalidate the *caller's* scope and recompose whatever
 * else that screen happens to hold alongside the `VMProvider` call. Here the read has a scope to
 * belong to.
 */
@PublishedApi
@Composable
internal fun <State : IState> ScreenState(
    viewState: StateFlow<State>, content: @Composable (state: State) -> Unit
) {
    val state by viewState.collectAsStateWithLifecycle()
    content(state)
}
