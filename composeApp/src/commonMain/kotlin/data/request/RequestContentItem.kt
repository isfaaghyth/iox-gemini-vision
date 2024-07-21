package data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestContentItem(
    @SerialName("parts") val parts: List<RequestContentPart>    
)