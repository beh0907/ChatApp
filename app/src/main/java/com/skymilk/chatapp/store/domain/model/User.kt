package com.skymilk.chatapp.store.domain.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val profileImageUrl: String? = null
)
