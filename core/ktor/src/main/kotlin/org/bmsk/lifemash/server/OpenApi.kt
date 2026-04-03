package org.bmsk.lifemash.server

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.openApi
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureOpenApi() {
    install(OpenApi) {
        info {
            title = "LifeMash API"
            version = "1.0.0"
            description = "LifeMash 백엔드 API 명세"
        }
    }
    routing {
        route("/openapi.json") {
            openApi()
        }
        get("/docs") {
            call.respondText(
                contentType = ContentType.Text.Html,
                text = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                      <title>LifeMash API Docs</title>
                      <meta charset="utf-8" />
                      <meta name="viewport" content="width=device-width, initial-scale=1" />
                    </head>
                    <body>
                      <script id="api-reference" data-url="/openapi.json"></script>
                      <script src="https://cdn.jsdelivr.net/npm/@scalar/api-reference"></script>
                    </body>
                    </html>
                """.trimIndent()
            )
        }
    }
}
