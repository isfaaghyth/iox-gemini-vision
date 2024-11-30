package data.request

import io.ktor.util.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ContentRequestBody(
    @SerialName("contents") val contents: List<RequestContentItem>
) {

    companion object {
        private const val PROMPT_NOTE = ". Gunakan Bahasa Indonesia yang ringkas saja tapi menyenangkan."

        fun requestBody(text: String, image: ByteArray): String {
            val parts = mutableListOf<RequestContentPart>()
            // command
            parts.add(RequestContentPart(text = text + PROMPT_NOTE))
            
            // attachment
            parts.add(
                RequestContentPart(
                    data = RequestInlineData(
                        mimeType = "image/jpeg",
                        data = image.encodeBase64()
                    )
                )
            )

            val body = ContentRequestBody(contents = listOf(RequestContentItem(parts)))
            return Json.encodeToString(body)
        }
    }
}
