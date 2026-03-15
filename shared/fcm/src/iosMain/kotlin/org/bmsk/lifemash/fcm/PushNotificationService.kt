package org.bmsk.lifemash.fcm

// iOS 타겟 추가(Phase 5) 시 APNs + GitLive Firebase iOS SDK로 구현
actual class PushNotificationService {
    actual fun registerToken(onToken: (String) -> Unit) {
        // TODO: Phase 5 — APNs 등록 + Firebase iOS SDK 연동
    }

    actual fun unregisterToken() {
        // TODO: Phase 5
    }
}
