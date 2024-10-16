package com.skymilk.chatapp.store.presentation.utils

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.skymilk.chatapp.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object CredentialUtil {

    suspend fun getGoogleIdTokenCredential(context: Context): GoogleIdTokenCredential? {

        try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_AUTH_WEB_CLIENT_ID)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val result = credentialManager.getCredential(context, request)

            return GoogleIdTokenCredential.createFrom(result.credential.data)
        } catch (e:Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun getKakaoToken(activity: Activity, kakaoUserApiClient: UserApiClient): OAuthToken? =
        suspendCancellableCoroutine { continuation ->

            kakaoUserApiClient.unlink {  }

            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    continuation.resume(null)
                } else if (token != null) {
                    continuation.resume(token)
                } else {
                    continuation.resume(null)
                }
            }

            if (kakaoUserApiClient.isKakaoTalkLoginAvailable(activity)) {
                kakaoUserApiClient.loginWithKakaoTalk(activity) { token, error ->

                    if (error != null) {
                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            continuation.resume(null)
                            return@loginWithKakaoTalk
                        }
                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
                    } else if (token != null) {
                        continuation.resume(token)
                    } else {
                        continuation.resume(null)
                    }
                }
            } else {
                kakaoUserApiClient.loginWithKakaoAccount(activity, callback = callback)
            }
        }
}