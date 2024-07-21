package di

import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import de.jensklingenberg.ktorfit.ktorfit
import di.network.GeminiApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import utils.KtorLogging

object GeminiClient {
    
    val app = ktorfit {
        baseUrl(GeminiApi.BASE_URL)
        httpClient(HttpClient {
            install(ContentNegotiation) {
                json(Json { isLenient = true; ignoreUnknownKeys = true })
            }
            install(Logging) {
                logger = KtorLogging
                level = LogLevel.ALL
            }
        })
        converterFactories(
            FlowConverterFactory(),
            CallConverterFactory()
        )
    }
}