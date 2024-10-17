package com.skymilk.chatapp.store.presentation.screen.auth

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kakao.sdk.auth.model.OAuthToken

sealed interface AuthEvent {

    data object CheckCurrentUser : AuthEvent

    data class SignInWithGoogle(val googleIdTokenCredential: GoogleIdTokenCredential?) : AuthEvent

    data class SignInWithKakao(val kakaoToken: OAuthToken?) : AuthEvent

    data class SignInWithEmailAndPassword(val email: String, val password: String) : AuthEvent

    data class SignUpWithEmailAndPassword(
        val name: String,
        val email: String,
        val password: String,
        val passwordConfirm: String
    ) : AuthEvent

    data object SignOut : AuthEvent

}