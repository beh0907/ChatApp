package com.skymilk.chatapp.store.domain.model

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    var id: String,
    var username: String,
    var email: String,
    var profileImageUrl: String? = null,
    var fcmToken: String
) : Parcelable {
    constructor() : this("", "", "", "", "")
}

// FirebaseUser 변환 함수
fun FirebaseUser.toUser(): User {
    return User(
        id = uid,
        username = displayName ?: "",
        email = email ?: "",
        profileImageUrl = photoUrl?.toString() ?: "",
        fcmToken = ""
    )
}