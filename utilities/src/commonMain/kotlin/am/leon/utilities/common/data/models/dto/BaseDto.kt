package am.leon.utilities.common.data.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class BaseDto(
    @SerialName("message") var message: String? = null
)