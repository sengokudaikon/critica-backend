package net.critika.infrastructure.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.uuid.UUID
import net.critika.infrastructure.AES256Util.decrypt
import net.critika.infrastructure.AES256Util.encrypt
import net.critika.persistence.user.repository.UserTokenRepository
import org.joda.time.LocalDateTime
import org.koin.core.annotation.Single
import java.io.FileInputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Single
class Security(
    private val tokenRepository: UserTokenRepository,
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

    @OptIn(ExperimentalEncodingApi::class)
    private fun generateSecretKey(): String {
        val dotenv = Dotenv.load()
        val base64SecretKey = dotenv["JWT_SECRET"]
            ?: throw SecurityException("JWT_SECRET environment variable is not set")
        return String(Base64.decode(base64SecretKey))
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
        token.let {
            return decrypt(it.token) == refreshToken
        }
    }

    suspend fun invalidateRefreshToken(userId: UUID) {
        tokenRepository.deleteTokens(userId)
    }

    fun configureFirebase(): FirebaseApp {
        val serviceAccount = FileInputStream("./serviceAccountKey.json")
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()
        return FirebaseApp.initializeApp(options)
    }
}
