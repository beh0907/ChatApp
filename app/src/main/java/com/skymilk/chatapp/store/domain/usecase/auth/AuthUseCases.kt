package com.skymilk.chatapp.store.domain.usecase.auth

data class AuthUseCases(
    val signInWithGoogle: SignInWithGoogle,
    val signInWithKakao: SignInWithKakao,
    val signInWithEmailAndPassword: SignInWithEmailAndPassword,
    val signUpWithEmailAndPassword: SignUpWithEmailAndPassword,
    val signOut: SignOut,
    val getCurrentUser: GetCurrentUser
)
