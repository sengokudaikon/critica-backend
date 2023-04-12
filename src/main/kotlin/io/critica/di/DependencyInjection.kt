package io.critica.di

import io.critica.config.AppConfig
import org.koin.dsl.module

val appModule = module {
    single { AppConfig.load() }
}