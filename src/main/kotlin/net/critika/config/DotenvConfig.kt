package net.critika.config

import io.github.cdimascio.dotenv.dotenv
import org.koin.core.annotation.Single

@Single
class DotenvConfig {
    fun get() = dotenv {
        directory = "./"
        filename = if (System.getenv("ENV") == "production") {
            ".env.production"
        } else if (System.getenv("ENV") == "dev") {
            ".env.development"
        } else {
            ".env"
        }
    }
}
