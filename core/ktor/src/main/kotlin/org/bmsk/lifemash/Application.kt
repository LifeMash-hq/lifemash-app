package org.bmsk.lifemash

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.bmsk.lifemash.db.DatabaseFactory
import org.bmsk.lifemash.server.*
import java.io.File

fun main() {
    loadEnvFile()
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, module = {
        DatabaseFactory.init()
        configureDI()
        configureSerialization()
        configureCors()
        configureStatusPages()
        configureAuthentication()
        configureRouting()
    }).start(wait = true)
}

private fun loadEnvFile() {
    val envFile = listOf(
        File(".env"),
        File("core/ktor/.env"),
    ).firstOrNull { it.exists() } ?: return
    envFile.readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .forEach { line ->
            val (key, value) = line.split("=", limit = 2).takeIf { it.size == 2 } ?: return@forEach
            if (System.getenv(key.trim()) == null) {
                System.setProperty(key.trim(), value.trim())
            }
        }
}
