package am.leon.utilities.common.domain.repository.remote

import am.leon.utilities.common.domain.models.request.LeonFileWrapper
import am.leon.utilities.helpers.inputField.IInputFieldKey
import kotlinx.serialization.KSerializer

interface IApiNetworkProvider {
    suspend fun <ResponseBody : Any, RequestBody> create(
        responseSerializer: KSerializer<ResponseBody>, pathUrl: String,
        headers: Map<IInputFieldKey, Any>? = null, queryParams: Map<IInputFieldKey, Any>? = null,
        requestBody: RequestBody?
    ): ResponseBody

    suspend fun <ResponseBody : Any> create(
        responseSerializer: KSerializer<ResponseBody>, pathUrl: String,
        headers: Map<IInputFieldKey, Any>? = null, queryParams: Map<IInputFieldKey, Any>? = null,
        requestBody: Map<IInputFieldKey, Any>?, filesMap: Map<IInputFieldKey, LeonFileWrapper>?
    ): ResponseBody

    suspend fun <ResponseBody : Any> read(
        responseSerializer: KSerializer<ResponseBody>, pathUrl: String,
        headers: Map<IInputFieldKey, Any>? = null, queryParams: Map<IInputFieldKey, Any>? = null
    ): ResponseBody

    suspend fun <ResponseBody : Any, RequestBody> update(
        responseSerializer: KSerializer<ResponseBody>, pathUrl: String,
        headers: Map<IInputFieldKey, Any>? = null, queryParams: Map<IInputFieldKey, Any>? = null,
        requestBody: RequestBody?
    ): ResponseBody

    suspend fun <ResponseBody : Any, RequestBody> delete(
        responseSerializer: KSerializer<ResponseBody>, pathUrl: String,
        headers: Map<IInputFieldKey, Any>? = null, queryParams: Map<IInputFieldKey, Any>? = null,
        requestBody: RequestBody? = null
    ): ResponseBody
}