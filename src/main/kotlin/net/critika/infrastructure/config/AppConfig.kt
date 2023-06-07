package net.critika.infrastructure.config

import io.github.cdimascio.dotenv.Dotenv
import org.koin.core.annotation.Single

@Single
class AppConfig {
    val dotenv = Dotenv.load()
    fun dbConfig(): DbConfig {
        return DbConfig(
            driver = dotenv["DB_DRIVER"],
            url = dotenv["DB_URL"],
            username = dotenv["DB_USER"],
            password = dotenv["DB_PASSWORD"],
        )
    }
}
