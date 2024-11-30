package data.api

import data.response.GeminiResponse
import di.NetworkClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import utils.GEMINI_API_KEY

interface GeminiApi {

    suspend fun request(request: String): Result<GeminiResponse>

    companion object {

        private const val GEMINI_PRO_VISION = "gemini-1.5-flash-latest"
        private const val URL = "v1beta/models/$GEMINI_PRO_VISION:generateContent?key=$GEMINI_API_KEY"

        fun create() = object : GeminiApi {
            override suspend fun request(request: String): Result<GeminiResponse> {
                return runCatching {
                    NetworkClient
                        .create
                        .post(URL) { setBody(request) }
                        .body()
                }
            }
        }
    }
}