@file:OptIn(ExperimentalContracts::class)

package am.leon.utilities.extentions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.firstOrNull
import am.leon.utilities.common.data.models.Resource
import am.leon.utilities.common.data.models.exception.LeonException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

// Resource status checks
fun <T> Resource<T>.isSuccess(): Boolean {
    contract { returns(true) implies (this@isSuccess is Resource.Success) }
    return this is Resource.Success
}

fun <T> Resource<T>.isFailure(): Boolean {
    contract { returns(true) implies (this@isFailure is Resource.Failure) }
    return this is Resource.Failure
}

fun <T> Resource<T>.isProgress(): Boolean {
    contract { returns(true) implies (this@isProgress is Resource.Progress) }
    return this is Resource.Progress
}

// Resource value extraction
fun <Model> Resource<Model>.getOrNull(): Model? = when (this) {
    is Resource.Success -> model
    is Resource.Progress -> partialData
    else -> null
}

fun Resource<*>.exceptionOrNull(): LeonException? = (this as? Resource.Failure)?.exception

fun Resource<*>.throwOnFailure() = (this as? Resource.Failure)?.let { throw it.exception }

fun <Model> Resource<Model>?.getOrThrow(): Model? = when (this) {
    is Resource.Success -> model
    is Resource.Progress -> partialData
    is Resource.Failure -> throw exception
    else -> null
}

fun <Model> Resource<Model>?.getOrElse(onFailure: (exception: LeonException) -> Model): Model? =
    when (this) {
        is Resource.Success -> model
        is Resource.Progress -> partialData
        is Resource.Failure -> onFailure(exception)
        else -> null
    }

fun <Model> Resource<Model>?.getOrDefault(defaultValue: Model): Model? = when (this) {
    is Resource.Success -> model
    is Resource.Progress -> partialData
    else -> defaultValue
}

suspend fun <Model> Resource<Model>.onFailure(action: suspend (exception: LeonException) -> Unit): Resource<Model> =
    this.also { if (it.isFailure()) action(it.exception) }

suspend fun <Model> Resource<Model>.onSuccess(action: suspend (value: Model) -> Unit): Resource<Model> =
    this.also { if (it.isSuccess()) action(it.model) }

suspend fun <Model> Resource<Model>.onProgress(action: suspend (isLoading: Boolean) -> Unit): Resource<Model> =
    this.also { if (it.isProgress()) action(it.loading) }

// Flow<Resource> extensions
suspend fun <Model> Flow<Resource<Model>>.getOrNull(): Model? =
    this.filterIsInstance<Resource.Success<Model>>()
        .firstOrNull()
        ?.model

suspend fun Flow<Resource<*>>.exceptionOrNull(): LeonException? =
    this.filterIsInstance<Resource.Failure>()
        .firstOrNull()
        ?.exception

suspend fun Flow<Resource<*>>.throwOnFailure() =
    this.filterIsInstance<Resource.Failure>()
        .firstOrNull()
        ?.exception
        ?.let { throw it }

suspend fun <Model> Flow<Resource<Model>>.getOrThrow(): Model? =
    this.filterNot { it is Resource.Progress }
        .firstOrNull()
        .getOrThrow()

suspend fun <Result, Model : Result> Flow<Resource<Model>>.getOrElse(onFailure: (exception: LeonException) -> Result): Result? =
    this.filterNot { it is Resource.Progress }
        .firstOrNull()
        .getOrElse(onFailure)

suspend fun <Result, Model : Result> Flow<Resource<Model>>.getOrDefault(defaultValue: Result): Result? =
    this.filterNot { it is Resource.Progress }
        .firstOrNull()
        .getOrDefault(defaultValue)

suspend fun <Model> Flow<Resource<Model>>.asResource(
    onSuccess: (suspend (value: Model) -> Unit)? = null,
    onFailure: (suspend (exception: LeonException) -> Unit)? = null,
    onProgress: (suspend (isLoading: Boolean) -> Unit)? = null,
) = this.collect { resource ->
    when {
        resource.isSuccess() && onSuccess != null -> resource.onSuccess(onSuccess)
        resource.isFailure() && onFailure != null -> resource.onFailure(onFailure)
        resource.isProgress() && onProgress != null -> resource.onProgress(onProgress)
    }
}