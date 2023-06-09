package net.critika.infrastructure.config

import io.github.cdimascio.dotenv.Dotenv
import org.koin.core.annotation.Single

@Single
data class DbConfig(
    var driver: String,
    var url: String,
    var username: String,
    var password: String,
) {
    val dotenv = Dotenv.load()
    init {
        driver = dotenv["DB_DRIVER"]
        url = dotenv["DB_URL"]
        username = dotenv["DB_USER"]
        password = dotenv["DB_PASSWORD"]
    }
}
