package am.leon.utilities.common.presentation.di

import am.leon.navigation.api.IDestination
import am.leon.navigation.api.INavigator
import am.leon.navigation.api.NavigationResultKey
import am.leon.navigation.events.NavigationEvent
import am.leon.utilities.common.presentation.appEvents.controller.IAppEventController
import am.leon.utilities.common.presentation.viewModel.base.IVMCore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

private class StubNavigator : INavigator {
    override val navigationEvents: SharedFlow<NavigationEvent> = MutableSharedFlow()
    override fun navigate(destination: IDestination) = Unit
    override fun popTo(destination: IDestination, inclusive: Boolean) = Unit
    override fun replaceCurrent(destination: IDestination) = Unit
    override fun setNewRoot(destination: IDestination) = Unit
    override fun switchTab(destination: IDestination) = Unit
    override fun <T> finish(key: NavigationResultKey<T>, value: T) = Unit
    override fun finish() = Unit
    override fun <T> observeResult(key: NavigationResultKey<T>): Flow<T> = emptyFlow()
}

/** What an app adds when its ViewModels need more than the library's two dependencies. */
private class AppCore(core: IVMCore, val analytics: String) : IVMCore by core

/**
 * A DI module is only worth shipping if it resolves. These build a real Koin container from it, so
 * a definition that cannot be satisfied fails here rather than on an app's first screen.
 */
class UtilitiesPresentationModuleTest {

    private val navigator = StubNavigator()

    private fun koin(): Koin = koinApplication {
        modules(utilitiesPresentationModule(), module { single<INavigator> { navigator } })
    }.koin

    @Test
    fun the_event_controller_is_one_instance_for_the_whole_app() {
        val koin = koin()

        // Every ViewModel and the host composable have to be looking at the same one, or a message
        // raised by a screen reaches a controller nothing is collecting.
        assertSame(koin.get<IAppEventController>(), koin.get<IAppEventController>())
    }

    @Test
    fun the_core_is_wired_from_the_app_s_navigator_and_that_controller() {
        val koin = koin()
        val core = koin.get<IVMCore>()

        assertSame(navigator, core.navigator)
        assertSame(koin.get<IAppEventController>(), core.appEventController)
    }

    @Test
    fun an_app_can_wrap_the_core_without_redeclaring_what_it_wraps() {
        val koin = koinApplication {
            modules(
                utilitiesPresentationModule(),
                module {
                    single<INavigator> { navigator }
                    single { AppCore(core = get(), analytics = "tracker") }
                }
            )
        }.koin

        val appCore = koin.get<AppCore>()

        assertSame(navigator, appCore.navigator, "delegated straight through")
        assertSame(koin.get<IAppEventController>(), appCore.appEventController)
    }

    @Test
    fun without_the_app_s_navigator_the_failure_names_it() {
        // INavigator belongs to the navigation library and is wired by the app - the same instance
        // it hands to NavigationHost. All an app gets when it forgets is this message, so it has to
        // name the type: Koin's own outer message says only "could not create IVMCore".
        val koin = koinApplication { modules(utilitiesPresentationModule()) }.koin

        val thrown = assertFailsWith<Exception> { koin.get<IVMCore>() }

        val messages = generateSequence<Throwable>(thrown) { it.cause }.mapNotNull { it.message }
        assertTrue(
            messages.any { "INavigator" in it }, "should name what is missing: ${messages.toList()}"
        )
    }
}
