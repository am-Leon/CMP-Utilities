package am.leon.utilities.helpers.encryption.domain.repository.keyStore.rsa

import am.leon.utilities.helpers.encryption.data.models.enums.KeyPurpose
import am.leon.utilities.helpers.encryption.domain.repository.keyStore.IKeyStoreHelper
import java.security.KeyPair
import java.security.PublicKey

interface IKeyStoreRSAHelper : IKeyStoreHelper {

    val transformation: String

    fun getPublicKeyFromBytes(keyBytes: ByteArray): PublicKey

    fun getEncodedPublicKey(publicKey: PublicKey): String

    fun getOrCreateKeyPair(
        keyAlias: String, keyPurpose: KeyPurpose = KeyPurpose.CRYPTOGRAPHIC
    ): KeyPair

    fun hasPrivateKeyEntry(keyAlias: String): Boolean
}