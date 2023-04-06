package io.critica.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.critica.config.JwtConfig
import io.critica.persistence.dao.UserTokenDao
import io.ktor.server.application.*

fun Application.configureSecurity(jwtConfig: JwtConfig, userTokenDao: UserTokenDao) {

    install(Authentication) {
        jwt {
            realm = jwtConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.issuer)
                    .acceptExpiresAt(jwtConfig.expiration)
                    .build()
            )
            validate {
                val userId = it.payload.getClaim("sub").asInt()
                val userToken = userTokenDao.findByUserId(userId)

                if (userToken != null) {
                    JWTPrincipal(it.payload)
                } else {
                    null
                }
            }
        }
    }
}

