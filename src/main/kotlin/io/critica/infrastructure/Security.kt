package io.critica.infrastructure

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.critica.infrastructure.AES256Util.decrypt
import io.critica.infrastructure.AES256Util.encrypt
import io.critica.persistence.repository.UserTokenRepository
import org.joda.time.LocalDateTime
import org.koin.core.annotation.Single
import java.security.SecureRandom
import java.util.*

@Single
class Security(
    private val tokenRepository: UserTokenRepository
) {
    private val jwtSecret = generateSecretKey()
    private val algorithm = Algorithm.HMAC256(jwtSecret)

    private val accessTokenValiditySeconds = 3600 // 1 hour
    private val refreshTokenValiditySeconds = 604800 // 7 days

    fun generateAccessToken(userId: UUID): String {
        val now = LocalDateTime.now()
        val expiry = now.plusSeconds(accessTokenValiditySeconds)
        val algorithm = Algorithm.HMAC256(jwtSecret)

        return JWT.create()
            .withIssuer("critica.io")
            .withSubject("access_token")
            .withClaim("userId", userId.toString())
            .withIssuedAt(now.toDate())
            .withExpiresAt(expiry.toDate())
            .sign(algorithm)
    }

    @Suppress("MagicNumber")
    private fun generateSecretKey(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32) // 256 bits
        random.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    suspend fun generateRefreshToken(userId: UUID): String {
        val now = LocalDateTime.now()
        val expiry = now.plusSeconds(refreshTokenValiditySeconds)
        val algorithm = Algorithm.HMAC256(jwtSecret)

        val token = JWT.create()
            .withIssuer("critica.io")
            .withSubject("refresh_token")
            .withClaim("userId", userId.toString())
            .withIssuedAt(now.toDate())
            .withExpiresAt(expiry.toDate())
            .sign(algorithm)

        tokenRepository.saveToken(userId, encrypt(token))
        return token
    }

    fun configureSecurity(): JWTVerifier {
        return JWT.require(algorithm).build()
    }

    suspend fun verifyRefreshToken(userId: UUID, refreshToken: String): Boolean {
        val token = tokenRepository.findByUserId(userId)
        token.let { return decrypt(it.token) == refreshToken }
    }

    suspend fun invalidateRefreshToken(userId: UUID) {
        tokenRepository.deleteTokens(userId)
    }
}

