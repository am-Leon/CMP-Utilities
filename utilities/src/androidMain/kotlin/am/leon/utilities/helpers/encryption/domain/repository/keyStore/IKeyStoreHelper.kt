package am.leon.utilities.helpers.encryption.domain.repository.keyStore

import am.leon.utilities.helpers.encryption.data.models.enums.KeyPurpose
import java.security.KeyStore

interface IKeyStoreHelper {

    val keyStore: KeyStore

    fun checkIfCryptographicKeyExist(keyAlias: String): Boolean
    fun deleteCryptographicKey(keyAlias: String)
    fun getKeyPurpose(keyPurpose: KeyPurpose): Int

    companion object {
        const val PROVIDER = "AndroidKeyStore"
    }
}