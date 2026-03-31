package org.bmsk.lifemash.server

import io.ktor.server.application.*
import org.bmsk.lifemash.di.coreModule
import org.koin.ktor.plugin.Koin

fun Application.configureDI() {
    install(Koin) {
        modules(coreModule)
    }
}
