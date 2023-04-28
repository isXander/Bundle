package dev.isxander.bundle.source.modrinth

import dev.isxander.bundle.ModMeta
import dev.isxander.bundle.source.RemoteModMeta
import dev.isxander.bundle.source.RemoteModSource
import dev.isxander.bundle.source.modrinth.request.BulkGetLatestVersionsFromHashesRequest
import dev.isxander.bundle.source.modrinth.response.BulkGetLatestVersionsFromHashesResponse
import dev.isxander.bundle.source.modrinth.response.ModVersionResponse
import dev.isxander.bundle.utils.UpdateCandidate
import dev.isxander.bundle.utils.httpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.quiltmc.loader.api.QuiltLoader

object ModrinthModSource : RemoteModSource {
    override suspend fun bulkGetLatest(local: List<ModMeta>): List<UpdateCandidate> {
        val response = httpClient.post {
            modrinthUrl("version_files/update")
            contentType(ContentType.parse("application/json"))
            setBody(BulkGetLatestVersionsFromHashesRequest(
                local.map { it.sha512 },
                BulkGetLatestVersionsFromHashesRequest.HashAlgorithm.SHA512,
                listOf("fabric", "quilt"),
                listOf(QuiltLoader.getRawGameVersion()),
            ))
        }.body<BulkGetLatestVersionsFromHashesResponse>()

        val candidates = mutableListOf<UpdateCandidate>()
        for (localMod in local) {
            val remoteModUpdate = response[localMod.sha512] ?: continue
            val remoteMod = remoteModUpdate.asModMeta()
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