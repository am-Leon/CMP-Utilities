package am.leon.utilities.common.domain.repository.local.keyValue

interface IStorageKeyValueFile {
    val storageKV: IStorageKeyValue
    suspend fun clearStorageFile()
    suspend fun deleteStorageFile(): Boolean
}