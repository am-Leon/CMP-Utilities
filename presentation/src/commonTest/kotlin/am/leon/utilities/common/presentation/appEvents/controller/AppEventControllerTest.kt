package am.leon.utilities.common.presentation.appEvents.controller

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppEventControllerTest {

    @Test
    fun indicator_stays_on_until_the_last_caller_is_done() {
        val controller = AppEventController()

        controller.setLoading(isLoading = true)  // call A
        controller.setLoading(isLoading = true)  // call B
        controller.setLoading(isLoading = false) // A finishes

        assertTrue(controller.isLoading.value, "B is still running")

        controller.setLoading(isLoading = false) // B finishes
        assertFalse(controller.isLoading.value)
    }

    @Test
    fun an_unmatched_off_cannot_push_the_count_below_zero() {
        val controller = AppEventController()

        controller.setLoading(isLoading = false)
        controller.setLoading(isLoading = false)
        controller.setLoading(isLoading = true)

        // Had the count gone to -2, this "on" would have left the indicator hidden.
        assertTrue(controller.isLoading.value)
    }

    @Test
    fun reset_clears_the_indicator_whoever_is_still_counted() {
        val controller = AppEventController()

        controller.setLoading(isLoading = true)
        controller.setLoading(isLoading = true)
        controller.resetLoading()

        assertFalse(controller.isLoading.value)

        // And the count really is zero, not just hidden: one "on" shows it again.
        controller.setLoading(isLoading = true)
        assertTrue(controller.isLoading.value)
        controller.setLoading(isLoading = false)
        assertFalse(controller.isLoading.value)
    }
}
