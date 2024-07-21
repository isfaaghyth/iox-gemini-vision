package data.repository

import data.request.ContentRequestBody
import data.response.GeminiResponse
import di.network.GeminiApi
import domain.interactor.GeminiRepository

class GeminiRepositoryImpl(private val api: GeminiApi) : GeminiRepository {

    override suspend fun request(content: String): Result<GeminiResponse> {
        return try {
            Result.success(
                api.generateContent(
                    ContentRequestBody.createTextOnlyRequest(content)
                )
            )
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun requestWithImage(content: String, image: ByteArray): Result<GeminiResponse> {
        return try {
            Result.success(
                api.generateVisionContent(
                    ContentRequestBody.createTextAndImageAttachmentRequest(content, image)
                )
            )
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}