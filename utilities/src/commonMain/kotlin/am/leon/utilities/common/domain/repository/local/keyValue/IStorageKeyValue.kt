package am.leon.utilities.common.domain.repository.local.keyValue

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

interface IStorageKeyValue {

    fun <Model> getDirect(
        storageType: IStorageType, key: IStorageKeyEnum, defaultValue: Model,
        serializer: KSerializer<Model>
    ): Model

    fun <Model> putDirect(
        storageType: IStorageType, key: IStorageKeyEnum, value: Model,
        serializer: KSerializer<Model>
    )

    fun deleteDirect(storageType: IStorageType, key: IStorageKeyEnum)

    suspend fun <Model> get(
        storageType: IStorageType, key: IStorageKeyEnum, defaultValue: Model,
        serializer: KSerializer<Model>
    ): Model

    suspend fun <Model> put(
        storageType: IStorageType, key: IStorageKeyEnum, value: Model,
        serializer: KSerializer<Model>
    )

    fun <Model> getFlow(
        storageType: IStorageType, key: IStorageKeyEnum, defaultValue: Model,
        serializer: KSerializer<Model>
    ): Flow<Model>

    suspend fun delete(storageType: IStorageType, key: IStorageKeyEnum)

    suspend fun clearAll(storageType: IStorageType)
}

/**
 * The call sites. Each one derives the serializer from the reified type and forwards it.
 *
 * A **nullable** `Model` resolves to a nullable serializer here, which is what
 * makes a stored `null` read back as `null` rather than as the default.
 */
inline fun <reified Model> IStorageKeyValue.getDirect(
    storageType: IStorageType, key: IStorageKeyEnum, defaultValue: Model
): Model = getDirect(storageType, key, defaultValue, serializer())

inline fun <reified Model> IStorageKeyValue.putDirect(
    storageType: IStorageType, key: IStorageKeyEnum, value: Model
): Unit = putDirect(storageType, key, value, serializer())

suspend inline fun <reified Model> IStorageKeyValue.get(
    storageType: IStorageType, key: IStorageKeyEnum, defaultValue: Model
): Model = get(storageType, key, defaultValue, serializer())

suspend inline fun <reified Model> IStorageKeyValue.put(
    storageType: IStorageType, key: IStorageKeyEnum, value: Model
): Unit = put(storageType, key, value, serializer())

inline fun <reified Model> IStorageKeyValue.getFlow(
    storageType: IStorageType, key: IStorageKeyEnum, defaultValue: Model
): Flow<Model> = getFlow(storageType, key, defaultValue, serializer())
