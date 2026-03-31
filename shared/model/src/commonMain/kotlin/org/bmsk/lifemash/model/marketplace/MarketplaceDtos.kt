package org.bmsk.lifemash.model.marketplace

import kotlinx.serialization.Serializable

@Serializable
data class SubmitBlockRequest(
    val name: String,
    val url: String,
    val description: String? = null,
    val iconUrl: String? = null,
    val toolsManifestUrl: String? = null,
)

@Serializable
data class MarketplaceBlockDto(
    val id: String,
    val name: String,
    val url: String,
    val description: String?,
    val iconUrl: String?,
    val status: String,
    val createdAt: Long,
    val toolsManifestUrl: String? = null,
    val toolDefinitions: String? = null,
)
