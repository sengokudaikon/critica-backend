package net.critika.infrastructure

import io.github.cdimascio.dotenv.Dotenv
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.SecureRandom
import java.security.Security
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AES256Util {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private val secretKey = generateSecretKey()

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    private fun generateSecretKey(): SecretKeySpec {
        val dotenv = Dotenv.load()
        val base64SecretKey = dotenv["JWT_SECRET"]
            ?: throw SecurityException("JWT_SECRET environment variable is not set")
        val keyBytes = Base64.getDecoder().decode(base64SecretKey)
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    fun encrypt(input: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION, "BC")
        val iv = ByteArray(cipher.blockSize).apply { SecureRandom().nextBytes(this) }
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        val combinedBytes = iv + encryptedBytes
        return Base64.getEncoder().encodeToString(combinedBytes)
    }

    fun decrypt(input: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION, "BC")
        val decodedBytes = Base64.getDecoder().decode(input)
        val iv = decodedBytes.sliceArray(0 until cipher.blockSize)
        val ivParameterSpec = IvParameterSpec(iv)
        val encryptedBytes = decodedBytes.sliceArray(cipher.blockSize until decodedBytes.size)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}