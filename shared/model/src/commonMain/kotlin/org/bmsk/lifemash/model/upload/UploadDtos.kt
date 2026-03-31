package org.bmsk.lifemash.model.upload

import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlRequest(val fileName: String, val contentType: String)

@Serializable
data class PresignedUrlResponse(val uploadUrl: String, val publicUrl: String)
