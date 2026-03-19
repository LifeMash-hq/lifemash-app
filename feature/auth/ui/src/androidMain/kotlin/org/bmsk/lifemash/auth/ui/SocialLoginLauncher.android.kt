package org.bmsk.lifemash.auth.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

@Composable
internal actual fun rememberKakaoLoginLauncher(onResult: (Result<String>) -> Unit): () -> Unit {
    val context = LocalContext.current

    return remember(context) {
        { loginWithKakao(context, onResult) }
    }
}

private fun loginWithKakao(context: Context, onResult: (Result<String>) -> Unit) {
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            onResult(Result.failure(error))
        } else if (token != null) {
            onResult(Result.success(token.accessToken))
        }
    }

    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    onResult(Result.failure(error))
                    return@loginWithKakaoTalk
                }
                // 카카오톡 로그인 실패 시 웹 로그인으로 폴백
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            } else if (token != null) {
                onResult(Result.success(token.accessToken))
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}
