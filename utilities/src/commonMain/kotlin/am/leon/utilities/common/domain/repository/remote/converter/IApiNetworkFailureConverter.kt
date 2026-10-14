package am.leon.utilities.common.domain.repository.remote.converter

import am.leon.utilities.common.data.models.exception.LeonException

interface IApiNetworkFailureConverter<ErrorBody> {
    suspend fun produceErrorBodyFailure(code: Int, errorBody: ErrorBody?): LeonException
    fun produceFailure(throwable: Throwable): LeonException
}