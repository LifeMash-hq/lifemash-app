package org.bmsk.lifemash.auth.impl

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

@Composable
internal actual fun rememberKakaoLoginLauncher(onResult: (Result<String>) -> Unit): () -> Unit {
    val context = LocalContext.current

    return remember(context) {
        { loginWithKakao(context, onResult) }
    }
}

private const val TAG = "KakaoLogin"

private fun loginWithKakao(context: Context, onResult: (Result<String>) -> Unit) {
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        Log.d(TAG, "callback: token=${token != null}, error=$error")
        if (error != null) {
            onResult(Result.failure(error))
        } else if (token != null) {
            onResult(Result.success(token.accessToken))
        }
    }

    Log.d(TAG, "isKakaoTalkAvailable=${UserApiClient.instance.isKakaoTalkLoginAvailable(context)}")

    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            Log.d(TAG, "loginWithKakaoTalk: token=${token != null}, error=$error")
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

private const val WEB_CLIENT_ID =
    "962012322315-o06kqnbd18269hfc7vp54537raoqs3vl.apps.googleusercontent.com"

@Composable
internal actual fun rememberGoogleLoginLauncher(onResult: (Result<String>) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    return remember(context) {
        {
            scope.launch {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(WEB_CLIENT_ID)
                    .setAutoSelectEnabled(false)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                runCatching {
                    val result = credentialManager.getCredential(context, request)
                    val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
                    credential.idToken
                }.onSuccess { idToken ->
                    onResult(Result.success(idToken))
                }.onFailure { error ->
                    if (error is GetCredentialCancellationException) {
                        onResult(Result.failure(error))
                    } else {
                        onResult(Result.failure(error))
                    }
                }
            }
        }
    }
}
