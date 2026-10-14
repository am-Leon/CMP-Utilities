package am.leon.utilities.helpers.encryption.domain.repository.keyStore.aes

import am.leon.utilities.helpers.encryption.domain.repository.keyStore.IKeyStoreHelper
import javax.crypto.SecretKey

interface IKeyStoreAESHelper : IKeyStoreHelper {

    val transformation: String

    fun getOrCreateSecretKey(keyAlias: String): SecretKey
}