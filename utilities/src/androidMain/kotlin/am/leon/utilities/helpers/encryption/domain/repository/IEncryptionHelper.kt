package am.leon.utilities.helpers.encryption.domain.repository

import am.leon.utilities.helpers.encryption.data.models.enums.KeyPurpose
import java.security.KeyPair
import javax.crypto.SecretKey

interface IEncryptionHelper {

    // -------------------------------------------- AES --------------------------------------------

    fun getOrCreateSecretKey(keyAlias: String): SecretKey
    fun getOrCreateSecretKey(keyMaterial: ByteArray): SecretKey

    fun encryptAES(keyAlias: String, textInBytes: ByteArray): ByteArray
    fun encryptAES(secretKey: SecretKey, textInBytes: ByteArray): ByteArray
    fun decryptAES(keyAlias: String, encryptedDataWithIV: ByteArray): ByteArray
    fun decryptAES(secretKey: SecretKey, encryptedDataWithIV: ByteArray): ByteArray


    // -------------------------------------------- RSA --------------------------------------------

    fun getOrCreateKeyPair(
        keyAlias: String, purpose: KeyPurpose = KeyPurpose.CRYPTOGRAPHIC
    ): KeyPair

    fun encryptRSA(keyAlias: String, textInBytes: ByteArray): ByteArray
    fun encryptRSA(keyPair: KeyPair, textInBytes: ByteArray): ByteArray
    fun decryptRSA(keyAlias: String, encryptedData: ByteArray): ByteArray
    fun decryptRSA(keyPair: KeyPair, encryptedData: ByteArray): ByteArray

    // -------------------------------------------- Common -----------------------------------------

    fun removeCryptographicKey(keyAlias: String)
}