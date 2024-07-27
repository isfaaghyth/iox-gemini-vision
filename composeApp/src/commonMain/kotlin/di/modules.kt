package di

import data.repository.GeminiRepositoryImpl
import di.network.GeminiApi
import domain.interactor.GeminiRepository
import domain.domain.GetContentWithImageUseCase
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
    single<GeminiApi> { GeminiClient.app.create() }
    single<GeminiRepository> { GeminiRepositoryImpl(get()) }
}