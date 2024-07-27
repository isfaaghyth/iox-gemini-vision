package domain.interactor

import data.response.GeminiResponse

interface GeminiRepository {

    suspend fun requestWithImage(content: String, image: ByteArray): Result<GeminiResponse>
}