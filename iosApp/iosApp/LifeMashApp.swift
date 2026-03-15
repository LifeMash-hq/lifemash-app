import SwiftUI
import LifeMashShared
import FirebaseCore

@main
struct LifeMashApp: App {
    init() {
        if let _ = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
           FirebaseApp.app() == nil {
            FirebaseApp.configure()
        }
        MainViewControllerKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
