package io.critica.infrastructure

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Key
import java.security.SecureRandom
import java.security.Security
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

object AES256Util {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private val secretKey: Key = SecretKeySpec(generateSecretKey().toByteArray(), ALGORITHM)

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    fun generateSecretKey(): String {
        val keyGenerator = KeyGenerator.getInstance("AES")
        val secureRandom = SecureRandom()
        keyGenerator.init(256, secureRandom)
        val secretKey = keyGenerator.generateKey()
        return Base64.getEncoder().encodeToString(secretKey.encoded)
    }

    fun encrypt(input: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION, "BC")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(input: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION, "BC")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.getDecoder().decode(input)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
