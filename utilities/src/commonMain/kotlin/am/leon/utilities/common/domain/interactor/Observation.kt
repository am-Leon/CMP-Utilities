package am.leon.utilities.common.domain.interactor

import am.leon.utilities.common.data.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmInline

/**
 * A never-completing stream of [Resource]s, as produced by an [ObserveUseCase].
 *
 * A type of its own rather than a bare [Flow], so the one-shot operators cannot take it by
 * mistake: `InteractorVM.combine` clears the loading indicator only when the combined flow
 * completes, which an observation never does, and `invokeIf(condition = false)` would substitute a
 * flow that completes at once. Neither compiles against this type.
 *
 * [flow] is the way out when the raw stream is what you want.
 */
@JvmInline
value class Observation<out Domain>(val flow: Flow<Resource<Domain>>)
