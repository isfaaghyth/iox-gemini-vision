package di.network

import data.response.GeminiResponse
import de.jensklingenberg.ktorfit.http.*
import utils.GEMINI_API_KEY

interface GeminiApi {
    
    @POST("v1beta/models/${GEMINI_PRO}:generateContent?key=${GEMINI_API_KEY}")
    suspend fun generateContent(@Body request: String): GeminiResponse
    
    @POST("v1beta/models/${GEMINI_PRO_VISION}:generateContent?key=${GEMINI_API_KEY}")
    suspend fun generateVisionContent(@Body request: String): GeminiResponse

    companion object {
        const val BASE_URL = "https://generativelanguage.googleapis.com/"
        
        private const val GEMINI_PRO = "gemini-pro"
        private const val GEMINI_PRO_VISION = "gemini-1.5-flash-latest"
    }
}