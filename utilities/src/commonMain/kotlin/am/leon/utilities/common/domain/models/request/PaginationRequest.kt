package am.leon.utilities.common.domain.models.request

import am.leon.utilities.helpers.inputField.IInputFieldError
import am.leon.utilities.helpers.inputField.IInputFieldKey
import am.leon.utilities.helpers.inputField.InputFieldKey

open class PaginationRequest<RequestBody>(
    open val page: Int = 1,
    open val isPaginationEnabled: Boolean = true,
) : IRequestValidation<RequestBody> {

    override val remoteRequest: RemoteRequest<RequestBody>
        get() = RemoteRequest(
            queries = hashMapOf<IInputFieldKey, Any>().apply {
                if (isPaginationEnabled) put(InputFieldKey.PAGE, page)
                else put(InputFieldKey.PAGINATION, InputFieldKey.ALL.title)
            }
        )

    override fun getRequestContracts(): Map<RequestContractType, Map<IInputFieldKey, Boolean>> =
        buildMap {
            put(RequestContractType.QUERIES, hashMapOf(InputFieldKey.PAGE to isPaginationEnabled))
        }

    override fun implementValidationForEachInput(): Map<IInputFieldKey, Set<IInputFieldError>> =
        buildMap {
            if (isPaginationEnabled)
                put(InputFieldKey.PAGE, Validator.Numbers.validatePositive(page))
        }
}