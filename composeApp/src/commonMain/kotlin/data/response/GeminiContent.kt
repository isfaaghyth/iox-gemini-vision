package data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiContent(
    @SerialName("parts") val parts: List<GeminiPart>
) {
    
    @Serializable
    data class GeminiPart(
        @SerialName("text") val text: String
    )
}