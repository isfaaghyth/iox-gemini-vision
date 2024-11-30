package data.repository

import data.api.GeminiApi
import data.request.ContentRequestBody
import data.response.GeminiResponse
import domain.interactor.GeminiRepository

class GeminiRepositoryImpl(private val api: GeminiApi) : GeminiRepository {

    override suspend fun requestWithImage(content: String, image: ByteArray): Result<GeminiResponse> {
        val body = ContentRequestBody.requestBody(content, image)
        return api.request(body)
    }
}