package am.leon.utilities.common.presentation.viewModel.base

import am.leon.navigation.api.INavigator
import am.leon.utilities.common.presentation.appEvents.controller.IAppEventController

/**
 * The app-wide dependencies every [ScreenVM] needs, behind one definition so adding one does not
 * mean editing every ViewModel's constructor.
 *
 * Constructor-injected rather than service-located, so a ViewModel can be built directly in a test
 * with fakes instead of a running Koin application.
 *
 * An interface, so an app can carry its own alongside these - an analytics tracker, a session
 * store, a feature-flag reader - by delegating the library's half:
 *
 * ```
 * class AppVMCore(core: IVMCore, val analytics: Analytics) : IVMCore by core
 *
 * // `core` is the one utilitiesPresentationModule() already declares.
 * single { AppVMCore(core = get(), analytics = get()) }
 * ```
 *
 * Delegating rather than subclassing is what keeps that compiling when this interface gains a
 * member: the delegate answers for it. The app's own base ViewModel takes `AppVMCore` and passes
 * it up to [ScreenVM], which never needs more than this.
 */
interface IVMCore {
    val navigator: INavigator
    val appEventController: IAppEventController
}

/**
 * The library's own, for an app that needs nothing beyond these two.
 *
 * A factory rather than a public class, so the implementation can change without breaking
 * consumers. `utilitiesPresentationModule()` already declares it; this is for an app that wires
 * its graph by hand.
 */
fun VMCore(navigator: INavigator, appEventController: IAppEventController): IVMCore =
    DefaultVMCore(navigator = navigator, appEventController = appEventController)

internal class DefaultVMCore(
    override val navigator: INavigator,
    override val appEventController: IAppEventController
) : IVMCore
