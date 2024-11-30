package di

import data.api.GeminiApi
import data.repository.GeminiRepositoryImpl
import domain.domain.GetContentWithImageUseCase
import domain.interactor.GeminiRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun appModule() = listOf(
    domainModule,
    dataModule
)

val domainModule = module {
    singleOf(::GetContentWithImageUseCase)
}

val dataModule = module {
    single<GeminiApi> { GeminiApi.create() }
    single<GeminiRepository> { GeminiRepositoryImpl(get()) }
}
