package am.leon.utilities.extentions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <T : Any?, L : Flow<T>> LifecycleOwner.observe(
    state: Lifecycle.State = Lifecycle.State.STARTED, sharedFlow: L, body: (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) {
            sharedFlow.collect {
                body.invoke(it)
            }
        }
    }
}

inline fun <T> LifecycleOwner.repeatOnStarted(
    crossinline block: suspend CoroutineScope.() -> T
) {
    var executed = false // flag to track if the block has been executed
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            if (!executed) {
                block()
                executed = true
            }
        }
    }
}

fun LifecycleOwner.updateViewInUiThread(action: () -> Unit) {
    repeatOnStarted {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main) {
                action()
            }
        }
    }
}
