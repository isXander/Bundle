package dev.isxander.bundle.utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

val httpClient: HttpClient = HttpClient(Java) {
    engine {
        threadsCount = 4
        pipelining = true
        protocolVersion = java.net.http.HttpClient.Version.HTTP_2
        config {

        }
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

suspend fun HttpClient.downloadFile(url: String, listener: suspend (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ ->}): ByteArray? =
    get(url) {
        onDownload { bytesSentTotal, contentLength -> listener(bytesSentTotal, contentLength) }
    }.takeIf { it.status.isSuccess() }?.body<ByteArray>()
