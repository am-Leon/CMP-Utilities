package am.leon.utilities.common.presentation.viewModel.exception

import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.extentions.IString
import am.leon.utilities.extentions.mapToCombinedString
import am.leon.utilities.generated.resources.Res
import am.leon.utilities.generated.resources.error_retrial_message
import am.leon.utilities.generated.resources.internal_server_error_with_code
import am.leon.utilities.generated.resources.internal_server_error_with_code_and_message
import am.leon.utilities.generated.resources.internet_connection_error_message
import am.leon.utilities.generated.resources.unknown_error_occur
import am.leon.utilities.generated.resources.unknown_error_occur_with_code
import am.leon.utilities.generated.resources.unknown_error_occur_with_code_and_message
import am.leon.utilities.helpers.inputField.IInputFieldError
import org.jetbrains.compose.resources.StringResource

/**
 * The library's own mapping from [LeonException] to [IExceptionHandlerEvent], and the floor under
 * every app's [IExceptionMapper] - its [map] always returns an event, never `null`.
 *
 * [LeonException] itself stays sealed - it is `@Serializable`, and a closed hierarchy is what
 * makes that work - so a new error identity rides in `Client.Unhandled` or `Unknown` and gets told
 * apart by an app's own mapper.
 *
 * The wording here is deliberately generic - no internet connection, server error with a code - and
 * reads the same in every product, which is why it stays in the library while everything about
 * *showing* it moved out to `ScreenVM`'s consumers.
 *
 * An app that wants its own words does not redefine the resource key on top of this one. That only
 * works for native Android consumers, where `R.string` merges and the app wins; a Compose
 * Multiplatform consumer's `Res` is generated per module against that module's own
 * `composeResources` and never merges, so the redefined key would simply go unread. The supported
 * route is an [IExceptionMapper] that returns the app's own
 * [IExceptionHandlerEvent.ShowMessage] for the cases it cares about and `null` for the rest.
 *
 * @param onUnknownInputError maps an app's own [IInputFieldError] to its message; see
 * [mapToCombinedString].
 */
class DefaultExceptionMapper(
    private val onUnknownInputError: (IInputFieldError) -> IString? = { null }
) : IExceptionMapper {

    override fun map(exception: LeonException): IExceptionHandlerEvent {
        return when (exception) {
            is LeonException.Client.ResponseValidation -> handleResponseValidationException(
                exception
            )

            is LeonException.Client.Unauthorized -> IExceptionHandlerEvent.AuthorizationRequired
            is LeonException.Client.Unhandled -> handleUnhandledException(exception)
            is LeonException.Local.IOOperation -> handleIOOperationException()
            is LeonException.Local.RequestValidation -> handleRequestValidationException(exception)
            is LeonException.Network.Retrial -> handleRetrialException()
            is LeonException.Server.InternalServerError -> handleInternalServerErrorException(
                exception
            )

            else -> handleUnknownException() // Handles these cases [Unknown, Network.Unhandled]
        }
    }

    private fun handleResponseValidationException(exception: LeonException.Client.ResponseValidation): IExceptionHandlerEvent {
        return if (exception.errors.isEmpty()) IExceptionHandlerEvent.ShowMessage(
            message = exception.message?.takeIf { it.isNotBlank() }?.let(IString::Raw)
                ?: getMessage(Res.string.unknown_error_occur)
        )
        else IExceptionHandlerEvent.HandleValidationErrors(
            errors = exception.errors.mapValues { IString.Raw(it.value) }
        )
    }

    private fun handleUnhandledException(exception: LeonException.Client.Unhandled): IExceptionHandlerEvent {
        val message = if (exception.message.isNullOrBlank()) getMessage(
            messageRes = Res.string.unknown_error_occur_with_code, exception.httpErrorCode
        )
        else getMessage(
            messageRes = Res.string.unknown_error_occur_with_code_and_message,
            exception.httpErrorCode, exception.message
        )
        return IExceptionHandlerEvent.ShowMessage(message)
    }

    private fun handleIOOperationException(): IExceptionHandlerEvent {
        return IExceptionHandlerEvent.ShowMessage(
            message = getMessage(Res.string.error_retrial_message), withRetryAction = true
        )
    }

    private fun handleRequestValidationException(exception: LeonException.Local.RequestValidation): IExceptionHandlerEvent {
        return if (exception.validationErrors.isNullOrEmpty()) IExceptionHandlerEvent.ShowMessage(
            message = getMessage(Res.string.unknown_error_occur)
        )
        else IExceptionHandlerEvent.HandleValidationErrors(
            errors = exception.validationErrors
                .mapValues { it.value.mapToCombinedString(onUnknownInputError) }
        )
    }

    private fun handleRetrialException(): IExceptionHandlerEvent {
        return IExceptionHandlerEvent.ShowMessage(
            message = getMessage(Res.string.internet_connection_error_message),
            withRetryAction = true
        )
    }

    private fun handleInternalServerErrorException(exception: LeonException.Server.InternalServerError): IExceptionHandlerEvent {
        val message = if (exception.message.isNullOrBlank()) getMessage(
            messageRes = Res.string.internal_server_error_with_code, exception.httpErrorCode
        )
        else getMessage(
            messageRes = Res.string.internal_server_error_with_code_and_message,
            exception.httpErrorCode, exception.message
        )

        return IExceptionHandlerEvent.ShowMessage(message)
    }

    private fun handleUnknownException(): IExceptionHandlerEvent {
        return IExceptionHandlerEvent.ShowMessage(message = getMessage(Res.string.unknown_error_occur))
    }

    private fun getMessage(messageRes: StringResource, vararg formatArgs: Any): IString {
        return IString.MultiplatformRes(messageRes, *formatArgs)
    }
}