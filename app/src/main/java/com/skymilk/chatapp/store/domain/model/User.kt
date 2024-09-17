package com.skymilk.chatapp.store.domain.model

import com.google.firebase.auth.FirebaseUser

data class User(
    var id: String,
    var username: String,
    var email: String,
    var profileImageUrl: String? = null
) {
    constructor() : this("", "", "")
}

// FirebaseUser 변환 함수
fun FirebaseUser.toUser(): User {
    return User(
        id = uid,
        username = displayName ?: "",
        email = email ?: "",
        profileImageUrl = photoUrl?.toString() ?: ""
    )
}