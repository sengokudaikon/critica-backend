package io.critica.di

import io.critica.config.AppConfig
import io.critica.persistence.repository.LobbyRepository
import io.critica.presentation.action.lobby.Lobby
import io.critica.presentation.controller.LobbyController
import org.koin.dsl.module

val appModule = module {
    single { AppConfig.load() }
    single { LobbyRepository() }
    single { Lobby(get()) }
    single { LobbyController(get()) }
}