package domain.mapper

import data.response.GeminiResponse
import domain.model.ContentModel

object GeminiMapper {

    fun map(response: GeminiResponse): ContentModel {
        val responseText = response.candidates
            .firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text

        return ContentModel(
            text = responseText ?: ""
        )
    }
}