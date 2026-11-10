package am.leon.utilities.common.presentation.viewModel.base

import am.leon.navigation.api.IDestination
import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.common.presentation.appEvents.AppEvent
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
import am.leon.utilities.helpers.inputField.IInputFieldKey

/**
 * **Reference implementation — what a consuming app writes once.**
 *
 * [ScreenVM] is the only class in this library an app extends, and after the exception refactor it
 * shows the user nothing of its own. This is where that gap is filled: how an error looks, how long
 * it stays, what the retry button says, and what becomes of a validation error no field on the
 * screen claimed. None of it is library policy — every line is a product decision, which is exactly
 * why it lives here and not in `:presentation`.
 *
 * Each screen's ViewModel then extends *this*, never [ScreenVM] directly.
 *
 * It sits in the test source set on purpose: it compiles against the real [ScreenVM], so it cannot
 * drift from the API the way a snippet in a README would. It is not published.
 *
 * What it does **not** handle, by design:
 * - [IExceptionHandlerEvent.AuthorizationRequired] never arrives here. [ScreenVM] raises
 *   [AppEvent.SessionExpired] instead, and the app decides once — wherever it collects app events,
 *   typically `AppEntry` — whether that means going straight to login or asking the user first.
 *   Doing it per ViewModel would run it again for every screen whose call got the same 401.
 */
internal abstract class AppBaseVM<Action : IAction, Event : IEvent, State : IState, Destination : IDestination>(
    initialState: State, destination: Destination, vmCore: AppVMCore
) : ScreenVM<Action, Event, State, Destination>(initialState, destination, vmCore) {

    /** Read once here, so no screen has to reach for it or know how it was injected. */
    protected val analytics: Analytics = vmCore.analytics

    /**
     * A real app names this from its own resources: `IString.MultiplatformRes(Res.string.try_again)`
     * on Compose Multiplatform, `IString.AndroidRes(R.string.try_again)` on native Android.
     */
    protected open val retryLabel: IString = IString.Raw("Try again")

    final override fun presentExceptionEvent(event: IExceptionHandlerEvent) {
        when (event) {
            is IExceptionHandlerEvent.ShowMessage -> emit(
                message = event.message,
                isDismissible = true,
                messageType = MessageType.ERROR,
                duration = if (event.withRetryAction) MessageDuration.INDEFINITE
                else MessageDuration.SHORT,
                action = MessageAction(retryLabel, ::onRetryAction).takeIf { event.withRetryAction }
            )

            // Only the keys this screen did not bind itself get here - ScreenVM already filtered by
            // boundFieldKeys and bindsDynamicField. IString.Combined keeps them as resources rather
            // than flattening them to text now, so they render in whatever locale is current when
            // the snackbar is actually shown.
            is IExceptionHandlerEvent.HandleValidationErrors -> emit(
                message = IString.Combined(event.errors.values.toSet()),
                isDismissible = true,
                messageType = MessageType.ERROR,
                duration = MessageDuration.LONG
            )

            else -> presentAppExceptionEvent(event)
        }
    }

    /**
     * The app's own cases - whatever its [IExceptionMapper] returned for an exception this library
     * has no notion of. Nothing of the library's own ever reaches here.
     */
    protected open fun presentAppExceptionEvent(event: IExceptionHandlerEvent) = Unit
}

// ---------------------------------------------------------------------------------------------
// One screen on top of it, to show the per-screen half.
// ---------------------------------------------------------------------------------------------

internal data object CheckoutState : IState

internal sealed interface CheckoutField : IInputFieldKey {
    data object Email : CheckoutField {
        override val title: String = "email"
    }

    data object Coupon : CheckoutField {
        override val title: String = "coupon"
    }
}

/** Something every screen needs that the library knows nothing about. */
internal interface Analytics {
    fun event(name: String)
}

/**
 * What an app writes when its ViewModels need more than the library's two dependencies.
 *
 * `by core` is the point: delegation rather than subclassing, so this keeps compiling when
 * [IVMCore] gains a member - the delegate answers for it. In DI:
 *
 * ```
 * // `core` comes from utilitiesPresentationModule(); only the app's own half is declared here.
 * single { AppVMCore(core = get(), analytics = get()) }
 * ```
 */
internal class AppVMCore(core: IVMCore, val analytics: Analytics) : IVMCore by core

/** The app's own exception case, kept `sealed` so its own `when` stays exhaustive. */
internal sealed interface CheckoutExceptionEvent : IExceptionHandlerEvent {
    data object PaymentRequired : CheckoutExceptionEvent
}

internal class CheckoutVM(vmCore: AppVMCore) :
    AppBaseVM<IAction, IEvent, CheckoutState, TestDestination>(
        CheckoutState, TestDestination, vmCore
    ) {
    /** [CheckoutField.Email] is shown on the field itself, so it must not also become a snackbar. */
    override val boundFieldKeys: Set<IInputFieldKey> = setOf(CheckoutField.Email)

    var emailError: IString? = null
        private set

    var paywallShown: Boolean = false
        private set

    override fun createExceptionHandler(
        onEvent: (IExceptionHandlerEvent) -> Unit
    ): IExceptionHandler = ExceptionHandler(handler = onEvent) { exception ->
        CheckoutExceptionEvent.PaymentRequired.takeIf {
            exception is LeonException.Client.Unhandled && exception.httpErrorCode == 402
        }
    }

    // A real screen would do emit { copy(emailError = errors[CheckoutField.Email]) }.
    override fun bindFieldErrors(errors: Map<IInputFieldKey, IString>) {
        emailError = errors[CheckoutField.Email]
    }

    override fun presentAppExceptionEvent(event: IExceptionHandlerEvent) {
        when (event) {
            is IExceptionHandlerEvent.HandleValidationErrors -> Unit

            is IExceptionHandlerEvent.UnHandledClientException -> Unit

            is CheckoutExceptionEvent.PaymentRequired -> {
                paywallShown = true
                analytics.event("paywall_shown")
            }
        }
    }

    override fun clearState() = Unit

    override fun onActionTrigger(action: IAction) = Unit

    fun raise(exception: LeonException) = handleException(exception)
}
