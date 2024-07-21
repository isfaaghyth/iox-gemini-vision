package data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestContentPart(
    @SerialName("text") val text: String = "",
    @SerialName("inlineData") val data: RequestInlineData? = null
)