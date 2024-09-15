package com.skymilk.chatapp.store.domain.usecase

data class AuthUseCases(
    val signInWithGoogle: SignInWithGoogle,
    val signInWithEmailAndPassword: SignInWithEmailAndPassword,
    val signOut: SignOut,
    val getCurrentUser: GetCurrentUser
)
