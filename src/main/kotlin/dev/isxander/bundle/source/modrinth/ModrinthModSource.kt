package dev.isxander.bundle.source.modrinth

import dev.isxander.bundle.Bundle
import dev.isxander.bundle.ctx.Version
import dev.isxander.bundle.mod.Mod
import dev.isxander.bundle.mod.ModMeta
import dev.isxander.bundle.source.RemoteModSource
import dev.isxander.bundle.source.modrinth.request.BulkGetLatestVersionsFromHashesRequest
import dev.isxander.bundle.source.modrinth.request.BulkGetVersionsFromHashesRequest
import dev.isxander.bundle.source.modrinth.request.HashAlgorithm
import dev.isxander.bundle.source.modrinth.response.BulkGetLatestVersionsFromHashesResponse
import dev.isxander.bundle.source.modrinth.response.BulkGetProjectsResponse
import dev.isxander.bundle.source.modrinth.response.BulkGetVersionsFromHashesResponse
import dev.isxander.bundle.utils.UpdateCandidate
import dev.isxander.bundle.utils.httpClient
import dev.isxander.bundle.utils.logger
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object ModrinthModSource : RemoteModSource {
    suspend fun bulkFillMeta(mods: List<Mod>) {
        logger.info("Getting mod metadata from Modrinth...")

        // first get current versions from hashes
        val versionsResponse = httpClient.post {
            modrinthUrl("version_files")
            contentType(ContentType.parse("application/json"))
            setBody(BulkGetVersionsFromHashesRequest(
                mods.map { it.file.sha512 },
                HashAlgorithm.SHA512
            ))
        }.body<BulkGetVersionsFromHashesResponse>()

        // then map project id to mod
        val projectIds = versionsResponse
            .mapValues { it.value.projectId }
            .mapKeys { (k, _) -> mods.find { it.file.sha512 == k } ?: error("API didn't return all files") }
            .entries.associate { (k, v) -> v to k }

        // then get projects from api
        val projectsResponse = httpClient.get {
            modrinthUrl("projects") {
                parameters["ids"] = projectIds.keys.joinToString(",", prefix = "[", postfix = "]") { "\"$it\"" }
            }
        }.body<BulkGetProjectsResponse>()

        // add meta to mods
        for (project in projectsResponse) {
            val mod = projectIds[project.id] ?: continue
            mod.meta = ModMeta(project.title, Version.of(versionsResponse[mod.file.sha512]!!.versionNumber))
        }
    }

    override suspend fun bulkGetLatest(local: List<Mod>): List<UpdateCandidate> {
        logger.info("Getting latest versions from Modrinth...")

        val response = httpClient.post {
            modrinthUrl("version_files/update")
            contentType(ContentType.parse("application/json"))
            setBody(BulkGetLatestVersionsFromHashesRequest(
                local.map { it.file.sha512 },
                HashAlgorithm.SHA512,
                listOf("fabric", "quilt"),
                listOf(Bundle.LOADER_CTX.gameVersion),
            ))
        }.body<BulkGetLatestVersionsFromHashesResponse>()

        val candidates = mutableListOf<UpdateCandidate>()
        for (localMod in local) {
            val remoteModUpdate = response[localMod.file.sha512] ?: continue
            val remoteMod = remoteModUpdate.asMod(localMod)
            candidates.add(UpdateCandidate(localMod, remoteMod))
        }

        return candidates
    }

    private fun HttpRequestBuilder.modrinthUrl(path: String, block: (URLBuilder).() -> Unit = {}) = url {
        protocol = URLProtocol.HTTPS
        host = "api.modrinth.com"
        path("v2/$path")
        block(this)
    }
}