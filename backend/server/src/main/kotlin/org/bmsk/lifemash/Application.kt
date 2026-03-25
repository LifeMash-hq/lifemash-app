package org.bmsk.lifemash

import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.bmsk.lifemash.db.DatabaseFactory
import org.bmsk.lifemash.plugins.*

fun main(args: Array<String>) {
    loadEnvFile()
    EngineMain.main(args)
}

private fun loadEnvFile() {
    val envFile = java.io.File(".env")
    if (!envFile.exists()) return
    envFile.readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") && '=' in it }
        .forEach { line ->
            val key = line.substringBefore('=').trim()
            val value = line.substringAfter('=').trim()
            if (System.getenv(key) == null && System.getProperty(key) == null) {
                System.setProperty(key, value)
            }
        }
}

fun Application.module() {
    DatabaseFactory.init()
    configureDI()
    configureSerialization()
    configureCors()
    configureStatusPages()
    configureAuthentication()
    configureRouting()
}
