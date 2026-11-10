package am.leon.utilities.common.presentation.viewModel.base

import am.leon.navigation.api.IDestination
import am.leon.navigation.api.INavigator
import am.leon.navigation.api.NavigationResultKey
import am.leon.navigation.events.NavigationEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.emptyFlow

/** Shared by every [ScreenVM] test in this package, so each one declares only what it is about. */
internal data object TestDestination : IDestination

internal class FakeNavigator : INavigator {
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
