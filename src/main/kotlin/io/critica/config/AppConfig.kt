package io.critica.config

import com.typesafe.config.ConfigFactory

data class AppConfig(
    val dbConfig: DbConfig,
    val jwtConfig: JwtConfig,
    val oauthConfig: OauthConfig
) {
    companion object {
        fun load(): AppConfig {
            val config = ConfigFactory.load("application.conf")

            return AppConfig(
                dbConfig = DbConfig(
                    url = config.getString("database.url"),
                    user = config.getString("database.user"),
                    password = config.getString("database.password"),
                ),
                jwtConfig = JwtConfig(
                    secret = config.getString("jwt.secret"),
                    issuer = config.getString("jwt.issuer"),
                    realm = config.getString("jwt.realm"),
                    audience = config.getString("jwt.audience"),
                    expiration = config.getLong("jwt.expiration"),
                ),
                oauthConfig = OauthConfig(
                    oauthClientId = config.getString("oauth.client.id"),
                    oauthClientSecret = config.getString("oauth.client.secret"),
                    oauthTokenUrl = config.getString("oauth.token.url"),
                    oauthUserInfoUrl = config.getString("oauth.userinfo.url"),
                    oauthAuthUrl = config.getString("oauth.auth.url"),
                    oauthJwkSetUrl = config.getString("oauth.jwkset.url"),
                    oauthRedirectUrl = config.getString("oauth.redirect.url"),
                )
            )
        }
    }
}