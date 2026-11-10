package am.leon.utilities.common.presentation.viewModel.exception

import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.extentions.IString
import am.leon.utilities.extentions.mapToCombinedString
import am.leon.utilities.helpers.inputField.IInputFieldError
import am.leon.utilities.helpers.inputField.IInputFieldKey

fun interface IExceptionHandler {
    fun handleException(exception: LeonException)
}

/**
 * Decides what a [LeonException] should do to the UI.
 *
 * This is the extension point: an app implements it to claim the exceptions it knows about and
 * returns `null` for the rest, which then fall through to [DefaultExceptionMapper]. Claiming a
 * case the library already handles is allowed too - the app's verdict wins.
 *
 * It is a `fun interface`, so an app's mapping is usually a lambda rather than a type:
 *
 * ```
 * ExceptionHandler(handler = onEvent) { exception ->
 *     ShopExceptionEvent.PaymentRequired.takeIf {
 *         exception is LeonException.Client.Unhandled && exception.httpErrorCode == 402
 *     }
 * }
 * ```
 */
fun interface IExceptionMapper {
    /** @return `null` to leave [exception] to the library's own mapping. */
    fun map(exception: LeonException): IExceptionHandlerEvent?
}

/**
 * What an [IExceptionMapper] decided a [LeonException] should do to the UI.
 *
 * Open on purpose: an app that maps an exception to a case this library has no notion of - a
 * paywall, a forced update - declares it here and reaches it through
 * `ScreenVM.onExceptionEvent`. Keep each app's own cases in its own `sealed` hierarchy so
 * its `when` stays exhaustive.
 */
interface IExceptionHandlerEvent {
    data class ShowMessage(
        val message: IString = IString.Empty, val withRetryAction: Boolean = false
    ) : IExceptionHandlerEvent

    data class HandleValidationErrors(val errors: Map<IInputFieldKey, IString>) :
        IExceptionHandlerEvent

    data object AuthorizationRequired : IExceptionHandlerEvent

    data class UnHandledClientException(val httpCode: Int, val message: String?) :
        IExceptionHandlerEvent
}

/**
 * Hands every [LeonException] to [mapper] first and to the library's own mapping for whatever the
 * mapper does not claim, then passes the verdict to [handler].
 *
 * Final on purpose: there is nothing here to override. An app changes the verdict by passing an
 * [IExceptionMapper], and an app that wants something else entirely implements [IExceptionHandler]
 * itself - neither needs to inherit from this class.
 *
 * @param onUnknownInputError maps an app's own [IInputFieldError] to its message; see
 * [mapToCombinedString].
 */
class ExceptionHandler(
    private val handler: (IExceptionHandlerEvent) -> Unit,
    onUnknownInputError: (IInputFieldError) -> IString? = { null },
    private val mapper: IExceptionMapper = IExceptionMapper { null }
) : IExceptionHandler {

    private val default = DefaultExceptionMapper(onUnknownInputError)

    override fun handleException(exception: LeonException) =
        handler.invoke(mapper.map(exception) ?: default.map(exception))
}
