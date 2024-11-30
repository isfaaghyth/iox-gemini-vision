package domain.domain

import domain.interactor.GeminiRepository
import domain.mapper.GeminiMapper
import domain.model.ContentModel

class GetContentWithImageUseCase(private val repository: GeminiRepository) {
    
    suspend operator fun invoke(content: String, image: ByteArray): ContentModel {
        return repository.requestWithImage(content, image)
            .map { GeminiMapper.map(it) }
            .getOrElse { ContentModel.empty() }
    }
}