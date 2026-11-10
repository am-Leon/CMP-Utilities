package am.leon.utilities.common.presentation.di

import am.leon.utilities.common.presentation.appEvents.controller.AppEventController
import am.leon.utilities.common.presentation.appEvents.controller.IAppEventController
import am.leon.utilities.common.presentation.viewModel.base.IVMCore
import am.leon.utilities.common.presentation.viewModel.base.VMCore
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Everything this library wires for itself, so an app adds one entry rather than copying
 * definitions into its own module:
 *
 * ```
 * startKoin { modules(utilitiesPresentationModule, appModule) }
 * ```
 *
 * It declares only what the library owns. `INavigator` is not in here - the navigation host owns
 * that - so the app must have declared it, or resolving [IVMCore] fails.
 *
 * An app whose ViewModels need more than these two dependencies **wraps** the [IVMCore] below
 * instead of replacing it:
 *
 * ```
 * single { AppVMCore(core = get(), analytics = get()) }
 * ```
 *
 * Both then live side by side: the app's ViewModels ask for `AppVMCore`, nothing asks for the bare
 * [IVMCore], and Koin never builds it. See [IVMCore] for why that wrapper delegates.
 *
 * A function rather than a `val`: Koin caches each `single` inside the module's own instance
 * factory, so one shared `Module` object loaded into two containers hands the second container the
 * first one's instances. That is invisible in an app that starts Koin once and obvious the moment
 * anything restarts it - a test suite, a sign-out that rebuilds the graph. Each call builds its own.
 */
fun utilitiesPresentationModule(): Module = module {
    single<IAppEventController> { AppEventController() }

    single<IVMCore> { VMCore(navigator = get(), appEventController = get()) }
}
