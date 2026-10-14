package am.leon.utilities.common.data.models

import am.leon.utilities.common.data.models.exception.LeonException

/**
 * [Resource] class is a wrapper sealed class to wrap th data between the app layers.
 *
 * @author Abduelrahman Elemam
 */
sealed class Resource<out Model> {
    /**
     * [Progress] is a data class that emits the loading state.
     */
    data class Progress<Model>(val loading: Boolean, val partialData: Model? = null) :
        Resource<Model>()

    /**
     * [Success] is a generic data class to wrap the succeeded data result.
     *
     * @param model the result data
     */
    data class Success<out Model>(val model: Model) : Resource<Model>()

    /**
     * [LeonException] is a data class to wrap the fail exception result.
     *
     * @param exception the exception result
     */
    data class Failure(val exception: LeonException) : Resource<Nothing>()

    companion object {
        fun <Model> loading(
            loading: Boolean = true, partialData: Model? = null
        ): Resource<Model> = Progress(loading, partialData)

        fun <Model> success(model: Model): Resource<Model> = Success(model)

        fun <Model> failure(exception: LeonException): Resource<Model> = Failure(exception)
    }
}