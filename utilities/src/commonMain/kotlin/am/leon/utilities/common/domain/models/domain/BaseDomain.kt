package am.leon.utilities.common.domain.models.domain

import kotlinx.serialization.Serializable

@Serializable
open class BaseDomain(
    var message: String = ""
)