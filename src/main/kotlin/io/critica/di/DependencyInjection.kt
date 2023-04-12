package io.critica.di

import io.critica.config.AppConfig
import io.critica.persistence.repository.*
import io.critica.presentation.controller.GameController
import io.critica.usecase.game.GameUseCase
import io.critica.usecase.lobby.LobbyUseCase
import io.critica.presentation.controller.LobbyController
import io.critica.usecase.game.EventUseCase
import io.github.cdimascio.dotenv.dotenv
import org.koin.dsl.module

val appModule = module {
    single { AppConfig.load() }
    single { PlayerRepository() }
    single { LobbyRepository() }
    single { GameRepository() }
    single { UserRepository() }
    single { EventRepository() }
    single { EventUseCase(get(), get(), get()) }
    single { GameUseCase(get(), get(), get()) }
    single { LobbyUseCase(get(), get(), get(), get()) }
    single { LobbyController(get()) }
    single { GameController(get(), get()) }
}

val dotenv = dotenv {
    directory = "./"
    if (System.getenv("ENV") == "production") {
        filename = ".env.production"
    } else if (System.getenv("ENV") == "dev") {
        filename = ".env.development"
    } else {
        filename = ".env"
    }
}
