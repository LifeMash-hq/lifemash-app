package org.bmsk.lifemash.plugins

import io.ktor.server.application.*
import org.bmsk.lifemash.di.resolveBackendModule
import org.koin.ktor.plugin.Koin

fun Application.configureDI() {
    install(Koin) {
        modules(resolveBackendModule())
    }
}
