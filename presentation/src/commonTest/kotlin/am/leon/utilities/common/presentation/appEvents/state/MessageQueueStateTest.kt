package am.leon.utilities.common.presentation.appEvents.state

import am.leon.utilities.extentions.IString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class MessageQueueStateTest {

    private fun snackbar(text: String) = IMessageEvent.Snackbar(message = IString.Raw(text))
    private fun dialog(text: String) = IMessageEvent.Dialog(message = IString.Raw(text))

    @Test
    fun the_first_message_goes_straight_on_screen() {
        val state = SnackbarState()
        val first = snackbar("first")

        state.show(first)

        assertSame(first, state.current)
    }

    @Test
    fun a_second_message_waits_instead_of_replacing_the_one_on_screen() {
        val state = SnackbarState()
        val first = snackbar("first")

        state.show(first)
        state.show(snackbar("second"))

        assertSame(first, state.current, "the one already on screen must not be pushed aside")
    }

    @Test
    fun dismiss_promotes_the_queue_in_the_order_it_arrived() {
        val state = SnackbarState()

        state.show(snackbar("first"))
        state.show(snackbar("second"))
        state.show(snackbar("third"))

        assertEquals(IString.Raw("first").value, (state.current?.message as IString.Raw).value)
        state.dismiss()
        assertEquals(IString.Raw("second").value, (state.current?.message as IString.Raw).value)
        state.dismiss()
        assertEquals(IString.Raw("third").value, (state.current?.message as IString.Raw).value)
    }

    @Test
    fun dismissing_the_last_message_clears_the_surface() {
        val state = SnackbarState()

        state.show(snackbar("only"))
        state.dismiss()

        assertNull(state.current)
    }

    @Test
    fun dismissing_an_empty_surface_is_harmless() {
        val state = SnackbarState()

        state.dismiss()

        assertNull(state.current)
    }

    /**
     * The aliases share an implementation, not an instance. A modal dialog and a transient
     * snackbar are meant to be on screen together, which a shared queue would prevent.
     */
    @Test
    fun a_snackbar_and_a_dialog_are_held_independently() {
        val snackbarState = SnackbarState()
        val dialogState = AlertDialogState()

        val snack = snackbar("saved")
        val confirm = dialog("are you sure?")
        snackbarState.show(snack)
        dialogState.show(confirm)

        assertSame(snack, snackbarState.current)
        assertSame(confirm, dialogState.current)
    }
}
