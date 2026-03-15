package org.bmsk.lifemash.fcm

/**
 * FCM(Android) / APNs(iOS) 토큰을 플랫폼별로 가져오는 expect/actual 인터페이스.
 * iOS 타겟 추가 시 iosMain actual 구현을 추가한다.
 */
expect class PushNotificationService {
    fun registerToken(onToken: (String) -> Unit)
    fun unregisterToken()
}
