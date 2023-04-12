package io.critica.config

data class OauthConfig(
    val oauthClientId: String,
    val oauthClientSecret: String,
    val oauthTokenUrl: String,
    val oauthUserInfoUrl: String,
    val oauthAuthUrl: String,
    val oauthJwkSetUrl: String,
    val oauthRedirectUrl: String,
)
