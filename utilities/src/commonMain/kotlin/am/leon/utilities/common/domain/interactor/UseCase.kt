package am.leon.utilities.common.domain.interactor

import am.leon.utilities.common.data.models.Resource
import am.leon.utilities.common.data.models.exception.LeonException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

/**
 * Acts as a contract for all the use cases in our application:
 * Abstract class for a Use Case (Interact in terms of Clean Architecture).
 * This abstraction represents an execution unit for different use cases (this means that any use
 * case in the application should implement this contract).
 *
 * @param Domain: the result returned after mapping the response to to the View
 * @param Body: a parameters class which will be consumed inside the [invoke] function
 *                in case we need extra data for our use case.
 *
 * @author Abduelrahman Elemam
 */
abstract class UseCase<out Domain, in Body> {

    abstract operator fun invoke(
        body: Body? = null, multipleInvoke: Boolean = false
    ): Flow<Resource<Domain>>

    //----------------------------------------------------------------------------------------------

    fun <Domain> executeLocal(
        multipleInvoke: Boolean,
        localExecution: suspend () -> Domain,
        performRemoteOperation: (Domain) -> Boolean = { false },
        remoteExecution: suspend () -> Domain? = { null },
        performCacheOperation: (Domain) -> Boolean = { false },
        cacheExecution: suspend (Domain) -> Unit = { },
        onComplete: (suspend (Resource<Domain>) -> Resource<Domain>)? = null
    ): Flow<Resource<Domain>> = execute(
        multipleInvoke = multipleInvoke,
        codeBlock = {
            // Execute local operation first...
            val localData = localExecution.invoke()

            if (performRemoteOperation.invoke(localData)) {
                val remoteData = remoteExecution.invoke()

                if (remoteData != null) {
                    if (performCacheOperation.invoke(remoteData))
                        cacheExecution.invoke(remoteData)

                    Resource.success(remoteData)
                } else {
                    // emit only local result...
                    Resource.success(localData)
                }
            } else {
                // emit only local result...
                Resource.success(localData)
            }
        },
        onComplete = onComplete
    )

    fun <Domain> executeRemote(
        multipleInvoke: Boolean,
        remoteExecution: suspend () -> Domain,
        performCacheOperation: (Domain) -> Boolean = { false },
        cacheExecution: suspend (Domain) -> Unit = { },
        onComplete: (suspend (Resource<Domain>) -> Resource<Domain>)? = null
    ): Flow<Resource<Domain>> = execute(
        multipleInvoke = multipleInvoke,
        codeBlock = {
            // Execute remote operation first...
            val remoteData = remoteExecution.invoke()

            // Validate if we need to cache...
            if (performCacheOperation.invoke(remoteData))
                cacheExecution.invoke(remoteData)

            Resource.success(remoteData)
        },
        onComplete = onComplete
    )

    private fun <Domain> execute(
        multipleInvoke: Boolean,
        codeBlock: suspend () -> Resource<Domain>,
        onComplete: (suspend (Resource<Domain>) -> Resource<Domain>)?
    ): Flow<Resource<Domain>> = channelFlow {
        send(wrapOnComplete(codeBlock.invoke(), onComplete))

        if (multipleInvoke.not())
            send(Resource.loading(loading = false))
    }.onStart {
        if (multipleInvoke.not())
            emit(Resource.loading())
    }.catch { exception ->
        val failureResource = exception as? LeonException
            ?: LeonException.Unknown(message = "Unknown error while executing the use case: $exception")

        emit(wrapOnComplete(Resource.failure(failureResource), onComplete))
        emit(Resource.loading(loading = false))
    }.flowOn(Dispatchers.IO)
}

private suspend fun <Domain> wrapOnComplete(
    resource: Resource<Domain>, onComplete: (suspend (Resource<Domain>) -> Resource<Domain>)?
): Resource<Domain> = onComplete?.invoke(resource) ?: resource