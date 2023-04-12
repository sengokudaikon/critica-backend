package io.critica.config

data class DbConfig(
    val driver: String,
    val url: String,
    val username: String,
    val password: String
)
