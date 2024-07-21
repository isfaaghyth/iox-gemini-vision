package domain.usecase

import domain.interactor.GeminiRepository
import domain.mapper.GeminiMapper
import domain.model.ContentModel

class GetContentUseCase(private val repository: GeminiRepository) {
    
    suspend operator fun invoke(content: String): ContentModel {
        return repository.request(content)
            .map { GeminiMapper.map(it) }
            .getOrElse { ContentModel.empty() }
    }
}