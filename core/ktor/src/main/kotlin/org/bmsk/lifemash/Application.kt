package org.bmsk.lifemash

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.bmsk.lifemash.db.DatabaseFactory
import org.bmsk.lifemash.server.*
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface

fun main() {
    loadEnvFile()
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    printLocalAddresses(port)
    embeddedServer(Netty, port = port, module = {
        DatabaseFactory.init()
        configureDI()
        configureSerialization()
        configureCors()
        configureStatusPages()
        configureAuthentication()
        configureOpenApi()
        configureRouting()
    }).start(wait = true)
}

private fun printLocalAddresses(port: Int) {
    val ips = NetworkInterface.getNetworkInterfaces().asSequence()
        .flatMap { it.inetAddresses.asSequence() }
        .filter { it is Inet4Address && !it.isLoopbackAddress }
        .map { it.hostAddress }
        .toList()
    println("┌──────────────────────────────────────")
    println("│ 📡 Backend running on port $port")
    ips.forEach { println("│    http://$it:$port") }
    println("│    http://localhost:$port")
    println("└──────────────────────────────────────")
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
