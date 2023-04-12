package io.critica.config

import com.typesafe.config.ConfigFactory
import io.github.cdimascio.dotenv.Dotenv

data class AppConfig(
    val dbConfig: DbConfig,
//    val jwtConfig: JwtConfig,
//    val oauthConfig: OauthConfig
) {
    companion object {
        fun load(): AppConfig {
            val config = ConfigFactory.load("application.conf")
            val dotenv = Dotenv.load()
            return AppConfig(
                dbConfig = DbConfig(
                    driver = dotenv["DB_DRIVER"],
                    url = dotenv["DB_URL"],
                    username = dotenv["DB_USER"],
                    password = dotenv["DB_PASSWORD"],
                ),
//                jwtConfig = JwtConfig(
//                    secret = config.getString("jwt.secret"),
//                    issuer = config.getString("jwt.issuer"),
//                    realm = config.getString("jwt.realm"),
//                    audience = config.getString("jwt.audience"),
//                    expiration = config.getLong("jwt.expiration"),
//                ),
//                oauthConfig = OauthConfig(
//                    oauthClientId = config.getString("oauth.client.id"),
//                    oauthClientSecret = config.getString("oauth.client.secret"),
//                    oauthTokenUrl = config.getString("oauth.token.url"),
//                    oauthUserInfoUrl = config.getString("oauth.userinfo.url"),
//                    oauthAuthUrl = config.getString("oauth.auth.url"),
//                    oauthJwkSetUrl = config.getString("oauth.jwkset.url"),
//                    oauthRedirectUrl = config.getString("oauth.redirect.url"),
//                )
            )
        }
    }
}
