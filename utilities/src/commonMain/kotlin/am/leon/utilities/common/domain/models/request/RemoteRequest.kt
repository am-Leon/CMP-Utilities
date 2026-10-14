package am.leon.utilities.common.domain.models.request

import am.leon.utilities.helpers.inputField.IInputFieldKey

data class RemoteRequest<RequestBody>(
    val body: HashMap<IInputFieldKey, Any>? = null,
    val bodyFiles: HashMap<IInputFieldKey, LeonFileWrapper>? = null,
    val queries: HashMap<IInputFieldKey, Any>? = null,
    val headers: HashMap<IInputFieldKey, Any>? = null,
    val pathVariables: HashMap<IInputFieldKey, Any>? = null,
    val bodyAsModel: RequestBody? = null
)