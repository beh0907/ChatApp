package com.skymilk.chatapp.store.domain.model

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    var id: String,
    var username: String,
    var email: String,
    var profileImageUrl: String? = null
) : Parcelable {
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

//User객체 Map 타입 변환
fun User.toMap(): Map<String, Any?> {
    return mapOf(
        "username" to username,
        "email" to email,
        "profileImageUrl" to profileImageUrl
    )
}