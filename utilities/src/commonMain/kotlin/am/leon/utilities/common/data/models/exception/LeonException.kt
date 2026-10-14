package am.leon.utilities.common.data.models.exception

import am.leon.utilities.helpers.inputField.IInputFieldError
import am.leon.utilities.helpers.inputField.IInputFieldKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class LeonException(@SerialName("message") open val errorMessage: String?) :
    Exception(errorMessage) {
    @Serializable
    @SerialName("Network")
    sealed class Network(@Transient override val message: String? = null) : LeonException(message) {
        @Serializable
        @SerialName("Retrial")
        data class Retrial(@Transient override val message: String? = null) : Network(message)

        @Serializable
        @SerialName("NetworkUnhandled")
        data class Unhandled(@Transient override val message: String? = null) : Network(message)
    }

    @Serializable
    @SerialName("Client")
    sealed class Client(@Transient override val message: String? = null) : LeonException(message) {
        @Serializable
        @SerialName("Unauthorized")
        data object Unauthorized : Client("Unauthorized Access.")

        @Serializable
        @SerialName("ResponseValidation")
        data class ResponseValidation(
            @SerialName("errors") val errors: Map<IInputFieldKey, String>,
            @Transient override val message: String? = null
        ) : Client(message)

        @Serializable
        @SerialName("ClientUnhandled")
        data class Unhandled(
            @SerialName("httpErrorCode") val httpErrorCode: Int,
            @Transient override val message: String? = null
        ) : Client(message = "Client unhandled error with code:${httpErrorCode}, and the failure reason: $message")
    }

    @Serializable
    @SerialName("Server")
    sealed class Server(@Transient override val message: String? = null) : LeonException(message) {
        @Serializable
        @SerialName("InternalServerError")
        data class InternalServerError(
            @SerialName("httpErrorCode") val httpErrorCode: Int,
            @Transient override val message: String? = null
        ) : Server(message = "Internal server error with code:${httpErrorCode}, and the failure reason: $message")
    }

    @Serializable
    @SerialName("Local")
    sealed class Local(@Transient override val message: String? = null) : LeonException(message) {
        @Serializable
        @SerialName("RequestValidation")
        data class RequestValidation(
            @SerialName("clazz") val clazz: String?,
            @Transient override val message: String? = null,
            @SerialName("validationErrors") val validationErrors: Map<IInputFieldKey, Set<IInputFieldError>>? = null
        ) : Local(
            buildString {
                append("There are some invalid inputs for this class: \"$clazz\"")
                message?.let { append(", message: $message") }
                validationErrors?.let { appendLine("Errors: $validationErrors") }
            }
        ) {
            val validationErrorsAsList: Map<IInputFieldKey, List<IInputFieldError>>?
                get() = validationErrors?.mapValues { (_, errorSet) -> errorSet.toList() }
        }

        @Serializable
        @SerialName("IOOperation")
        data class IOOperation(@Transient override val message: String? = null) : Local(message)
    }

    @Serializable
    @SerialName("Unknown")
    data class Unknown(@Transient override val message: String? = null) : LeonException(message)
}