package domain.interactor

import data.response.GeminiResponse

interface GeminiRepository {
    
    suspend fun request(content: String): Result<GeminiResponse>
    suspend fun requestWithImage(content: String, image: ByteArray): Result<GeminiResponse>
}