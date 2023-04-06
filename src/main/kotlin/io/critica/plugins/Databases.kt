package io.critica.plugins

import io.critica.config.DbConfig
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases(dbConfig: DbConfig): Database {
    return Database.connect(
        url = dbConfig.url,
        driver = "org.postgresql.Driver",
        user = dbConfig.user,
        password = dbConfig.password
    )
}

fun Application.configureMigrations(dbConfig: DbConfig) {
    val flyway = Flyway.configure().dataSource(
        dbConfig.url,
        dbConfig.user,
        dbConfig.password
    ).load()
    flyway.migrate()
}