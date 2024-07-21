import org.koin.core.context.startKoin
import di.appModule

fun initKoin() {
    startKoin {
        modules(appModule())
    }
}