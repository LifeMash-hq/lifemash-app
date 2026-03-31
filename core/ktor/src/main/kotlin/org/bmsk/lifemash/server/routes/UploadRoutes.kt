package org.bmsk.lifemash.server.routes

import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bmsk.lifemash.model.upload.PresignedUrlRequest
import org.bmsk.lifemash.upload.UploadService
import org.koin.ktor.ext.inject

fun Route.uploadRoutes() {
    val uploadService by inject<UploadService>()

    authenticate("auth-jwt") {
        route("/upload") {
            post("/presigned-url") {
                val request = call.receive<PresignedUrlRequest>()
                call.respond(uploadService.generatePresignedUrl(request.fileName, request.contentType))
            }
        }
    }
}
