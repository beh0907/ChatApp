package com.skymilk.chatapp.store.presentation.navigation

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.navigation.NavType
import com.skymilk.chatapp.store.data.dto.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CustomNavType {

    //네비게이션 간 데이터 이동 타입 설정
    val UserType = object : NavType<User>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): User? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(key, User::class.java)
            } else {
                bundle.getParcelable(key)
            }
        }

        override fun parseValue(value: String): User {
            return Json.decodeFromString<User>(value)
        }

        override fun serializeAsValue(value: User): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: User) {
            bundle.putParcelable(key, value)
        }
    }
}