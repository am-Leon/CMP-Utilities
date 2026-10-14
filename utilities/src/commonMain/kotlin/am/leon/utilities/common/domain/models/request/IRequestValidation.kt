package am.leon.utilities.common.domain.models.request

import am.leon.utilities.common.data.models.exception.LeonException
import am.leon.utilities.common.domain.models.domain.IInitialState
import am.leon.utilities.helpers.inputField.IInputFieldError
import am.leon.utilities.helpers.inputField.IInputFieldKey
import am.leon.utilities.helpers.inputField.InputFieldError

interface IRequestValidation<RequestBody> {

    val remoteRequest: RemoteRequest<RequestBody>

    fun getRequestContracts(): Map<RequestContractType, Map<IInputFieldKey, Boolean>>

    fun implementValidationForEachInput(): Map<IInputFieldKey, Set<IInputFieldError>>

    fun getOptionalKeys(contractType: RequestContractType): Map<IInputFieldKey, Any> =
        with(remoteRequest) {
            val requestContract = getRequestContracts()[contractType] ?: emptyMap()
            val requestMap = obtainRequestMap()
            return requestContract
                .filterNot { it.value }
                .mapNotNull { entry ->
                    val key = entry.key
                    val value = requestMap[key]
                    if (value != null) key to value else null
                }
                .toMap()
        }

    fun validateRequestContract(): Unit = with(remoteRequest) {
        val contract = getRequestContracts()
        validateMissingKeys(contract).apply {
            if (this.isNotEmpty()) throw LeonException.Local.RequestValidation(
                clazz = this@IRequestValidation::class.simpleName,
                message = "Your request contract is not completed, please complete missing keys.",
                validationErrors = this
            )
        }

        implementValidationForEachInput().apply {
            val filtered = filter { it.value.isNotEmpty() }

            if (filtered.isNotEmpty()) throw LeonException.Local.RequestValidation(
                clazz = this@IRequestValidation::class.simpleName, validationErrors = filtered
            )
        }
    }

    private fun validateMissingKeys(
        contractKeys: Map<RequestContractType, Map<IInputFieldKey, Boolean>>
    ): Map<IInputFieldKey, Set<IInputFieldError>> {
        val errorsMap = mutableMapOf<IInputFieldKey, Set<IInputFieldError>>()
        val requestMap = obtainRequestMap()

        contractKeys.forEach { (_, keyMap) ->
            keyMap.forEach { (key, isRequired) ->
                if (isRequired) {
                    val requestValue = requestMap[key]

                    if (key !in requestMap) errorsMap[key] =
                        mutableSetOf(InputFieldError.REQUEST_KEY_MISSING_FROM_REQUEST)

                    val errorReason = getErrorReason(requestValue)
                    if (errorReason != null)
                        errorsMap[key] = mutableSetOf(errorReason)
                }
            }
        }
        return errorsMap
    }

    private fun getErrorReason(value: Any?): IInputFieldError? {
        when (value) {
            is String -> if (value.isEmpty())
                return InputFieldError.EMPTY

            is Collection<*> -> if (value.isEmpty())
                return InputFieldError.EMPTY

            is Map<*, *> -> if (value.isEmpty())
                return InputFieldError.EMPTY

            is Int, is Float, is Double -> if ((value == 0.0) or (value == -1))
                return InputFieldError.INITIAL_STATE

            is ByteArray -> if (value.isEmpty())
                return InputFieldError.EMPTY

            is IInitialState -> if (value.isInitialState())
                return InputFieldError.INITIAL_STATE

            else -> if (value == null)
                return InputFieldError.REQUIRED
        }
        return null
    }

    private fun obtainRequestMap(): Map<IInputFieldKey, Any> = with(remoteRequest) {
        return@with hashMapOf<IInputFieldKey, Any>().apply {
            body?.let { putAll(it) }
            bodyFiles?.let { putAll(it) }
            queries?.let { putAll(it) }
            headers?.let { putAll(it) }
            pathVariables?.let { putAll(it) }
        }
    }
}