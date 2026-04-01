import Foundation
import GoogleSignIn
import ComposeApp

class GoogleSignInHelper {

    static let shared = GoogleSignInHelper()

    private let webClientId = "9652613568-l2ql2fgsrp74u29q1q2hiqc4gvbaoc0g.apps.googleusercontent.com"

    private init() {}

    func configure() {
        GoogleSignInBridgeHelper.shared.setSignInHandler {
            self.signIn()
        }
    }

    private func signIn() {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootViewController = windowScene.windows.first?.rootViewController else {
            GoogleSignInBridgeHelper.shared.onSignInComplete(idToken: nil)
            return
        }

        // Find the topmost presented view controller
        var topController = rootViewController
        while let presented = topController.presentedViewController {
            topController = presented
        }

        let config = GIDConfiguration(clientID: webClientId)
        GIDSignIn.sharedInstance.configuration = config

        GIDSignIn.sharedInstance.signIn(withPresenting: topController) { result, error in
            if let error = error {
                print("Google Sign-In error: \(error.localizedDescription)")
                GoogleSignInBridgeHelper.shared.onSignInComplete(idToken: nil)
                return
            }

            guard let idToken = result?.user.idToken?.tokenString else {
                print("Google Sign-In: No ID token received")
                GoogleSignInBridgeHelper.shared.onSignInComplete(idToken: nil)
                return
            }

            GoogleSignInBridgeHelper.shared.onSignInComplete(idToken: idToken)
        }
    }
}
