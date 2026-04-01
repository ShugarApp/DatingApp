import SwiftUI
import ComposeApp
import GoogleSignIn

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    init() {
        InitKoinKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    if GIDSignIn.sharedInstance.handle(url) {
                        return
                    }
                    ExternalUriHandler.shared.onNewUri(uri: url.absoluteString)
                }
        }
    }
}
