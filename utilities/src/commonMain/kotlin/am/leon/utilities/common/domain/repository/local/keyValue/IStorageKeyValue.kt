package am.leon.utilities.common.domain.repository.local.keyValue

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

interface IStorageKeyValue {

    fun <Model> getDirect(
        key: IStorageKeyEnum, defaultValue: Model, serializer: KSerializer<Model>
    ): Model

    fun <Model> putDirect(
        key: IStorageKeyEnum, value: Model, serializer: KSerializer<Model>
    )

    fun deleteDirect(key: IStorageKeyEnum)

    suspend fun <Model> get(
        key: IStorageKeyEnum, defaultValue: Model, serializer: KSerializer<Model>
    ): Model

    suspend fun <Model> put(
        key: IStorageKeyEnum, value: Model, serializer: KSerializer<Model>
    )

    fun <Model> getFlow(
        key: IStorageKeyEnum, defaultValue: Model, serializer: KSerializer<Model>
    ): Flow<Model>

    suspend fun delete(key: IStorageKeyEnum)
}

/**
 * The call sites. Each one derives the serializer from the reified type and
 * forwards it, so storage reads the same as it always did.
 *
 * A **nullable** `Model` resolves to a nullable serializer here, which is what
 * makes a stored `null` read back as `null` rather than as the default.
 */
inline fun <reified Model> IStorageKeyValue.getDirect(
    key: IStorageKeyEnum, defaultValue: Model
): Model = getDirect(key, defaultValue, serializer())

inline fun <reified Model> IStorageKeyValue.putDirect(
    key: IStorageKeyEnum, value: Model
): Unit = putDirect(key, value, serializer())

suspend inline fun <reified Model> IStorageKeyValue.get(
    key: IStorageKeyEnum, defaultValue: Model
): Model = get(key, defaultValue, serializer())

suspend inline fun <reified Model> IStorageKeyValue.put(
    key: IStorageKeyEnum, value: Model
): Unit = put(key, value, serializer())

inline fun <reified Model> IStorageKeyValue.getFlow(
    key: IStorageKeyEnum, defaultValue: Model
): Flow<Model> = getFlow(key, defaultValue, serializer())
