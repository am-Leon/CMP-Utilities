package am.leon.utilities.common.presentation.viewModel.exception

import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.extentions.IString
import am.leon.utilities.extentions.toStringContent
import am.leon.utilities.helpers.inputField.IInputFieldError
import am.leon.utilities.helpers.inputField.IInputFieldKey
import am.leon.utilities.helpers.inputField.InputFieldError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Stands in for what an app owns: its own error type, its own key, its own exception event, and
 * its own mapper. If any of these stop compiling, the library has closed a door on its consumers.
 */
private data object CouponExpired : IInputFieldError {
    override val title: String = "coupon_expired"
}

private data class ShopFieldKey(override val title: String) : IInputFieldKey

private sealed interface ShopExceptionEvent : IExceptionHandlerEvent {
    data object PaymentRequired : ShopExceptionEvent
}

/** The app claims the one exception it knows about and leaves the rest to the library. */
private val shopMapper = IExceptionMapper { exception ->
    ShopExceptionEvent.PaymentRequired.takeIf {
        exception is LeonException.Client.Unhandled && exception.httpErrorCode == 402
    }
}

private fun shopHandler(
    handler: (IExceptionHandlerEvent) -> Unit,
    onUnknownInputError: (IInputFieldError) -> IString? = { null }
): IExceptionHandler = ExceptionHandler(handler, onUnknownInputError, shopMapper)

class ExceptionHandlerExtensionTest {

    private fun eventFor(
        exception: LeonException,
        handlerOf: ((IExceptionHandlerEvent) -> Unit) -> IExceptionHandler
    ): IExceptionHandlerEvent {
        var captured: IExceptionHandlerEvent? = null
        handlerOf { captured = it }.handleException(exception)
        return requireNotNull(captured) { "the handler produced no event" }
    }

    @Test
    fun an_app_can_map_an_exception_to_a_case_of_its_own() {
        val event = eventFor(LeonException.Client.Unhandled(httpErrorCode = 402)) {
            shopHandler(it)
        }

        assertEquals(ShopExceptionEvent.PaymentRequired, event)
    }

    @Test
    fun the_library_mapping_still_applies_to_everything_the_app_does_not_claim() {
        val event = eventFor(LeonException.Client.Unauthorized) { shopHandler(it) }

        assertEquals(IExceptionHandlerEvent.AuthorizationRequired, event)
    }

    @Test
    fun an_app_can_overrule_the_library_on_a_case_it_already_handles() {
        // Unauthorized is one of the library maps itself; a mapper that claims it still wins.
        val event = eventFor(LeonException.Client.Unauthorized) { onEvent ->
            ExceptionHandler(onEvent) { ShopExceptionEvent.PaymentRequired }
        }

        assertEquals(ShopExceptionEvent.PaymentRequired, event)
    }

    @Test
    fun an_app_can_put_its_own_words_on_a_message_the_library_already_has() {
        // The supported route for an app that dislikes the library's wording: its own ShowMessage,
        // not a resource key redefined on top of the library's - CMP consumers never merge those.
        val event = eventFor(LeonException.Network.Retrial()) { onEvent ->
            ExceptionHandler(onEvent) { exception ->
                IExceptionHandlerEvent.ShowMessage(
                    message = IString.Raw("You are offline"), withRetryAction = true
                ).takeIf { exception is LeonException.Network.Retrial }
            }
        }

        val message = assertIs<IExceptionHandlerEvent.ShowMessage>(event)
        assertEquals("You are offline", assertIs<IString.Raw>(message.message).value)
    }

    @Test
    fun an_app_s_own_field_error_reaches_its_own_message() {
        val exception = LeonException.Local.RequestValidation(
            clazz = "CheckoutRequest",
            validationErrors = mapOf(ShopFieldKey("coupon") to setOf(CouponExpired))
        )

        val event = eventFor(exception) { onEvent ->
            shopHandler(onEvent) { error ->
                IString.Raw("That coupon has expired").takeIf { error == CouponExpired }
            }
        }

        val errors = assertIs<IExceptionHandlerEvent.HandleValidationErrors>(event).errors
        val combined = assertIs<IString.Combined>(errors.values.single())
        val raw = assertIs<IString.Raw>(combined.strings.single())
        assertEquals("That coupon has expired", raw.value)
    }

    @Test
    fun without_a_mapping_an_unknown_field_error_falls_back_instead_of_throwing() {
        // The fallback is what makes a custom error renderable; the generic string is the floor.
        val fallsBack = CouponExpired.toStringContent()
        val mapped = CouponExpired.toStringContent { IString.Raw("mine") }

        assertIs<IString.MultiplatformRes>(fallsBack)
        assertEquals("mine", assertIs<IString.Raw>(mapped).value)
    }

    @Test
    fun a_known_error_type_ignores_the_fallback() {
        val message = InputFieldError.REQUIRED.toStringContent { IString.Raw("should not be used") }

        assertTrue(message is IString.MultiplatformRes)
    }
}
