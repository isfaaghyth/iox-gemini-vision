import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        ProvidersKt.doInitKoin()
    }

    var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}