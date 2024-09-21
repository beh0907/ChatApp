package com.skymilk.chatapp.utils

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuthException
import com.skymilk.chatapp.R
import java.io.FileInputStream
import java.util.Arrays


object FirebaseUtil {

    // FirebaseAuthException에 따른 사용자 친화적인 오류 메시지를 반환
    fun getErrorMessage(exception: FirebaseAuthException?): String {
        return when (exception?.errorCode) {
            "ERROR_INVALID_CUSTOM_TOKEN" -> "잘못된 커스텀 토큰입니다."
            "ERROR_CUSTOM_TOKEN_MISMATCH" -> "커스텀 토큰이 맞지 않습니다."
            "ERROR_INVALID_CREDENTIAL" -> "잘못된 인증 정보입니다."
            "ERROR_INVALID_EMAIL" -> "유효하지 않은 이메일 주소입니다."
            "ERROR_WRONG_PASSWORD" -> "비밀번호가 잘못되었습니다."
            "ERROR_USER_MISMATCH" -> "인증된 사용자와 일치하지 않습니다."
            "ERROR_REQUIRES_RECENT_LOGIN" -> "최근 로그인 필요. 다시 로그인 해주세요."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "다른 계정으로 이미 가입된 이메일입니다."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "이미 사용 중인 이메일 주소입니다."
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "이 인증 정보는 이미 다른 계정에 연결되어 있습니다."
            "ERROR_USER_DISABLED" -> "이 계정은 비활성화되었습니다."
            "ERROR_USER_TOKEN_EXPIRED" -> "세션이 만료되었습니다. 다시 로그인하세요."
            "ERROR_USER_NOT_FOUND" -> "이 계정은 존재하지 않습니다."
            "ERROR_OPERATION_NOT_ALLOWED" -> "이 작업은 허용되지 않습니다."
            "ERROR_WEAK_PASSWORD" -> "비밀번호가 너무 약합니다."
            else -> "알 수 없는 오류가 발생했습니다. 다시 시도해주세요."
        }
    }

    fun getAccessToken(context: Context): String {
        val inputSteam = context.resources.openRawResource(R.raw.service_account)
        val googleCredentials = GoogleCredentials.fromStream(inputSteam)
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        return googleCredentials.refreshAccessToken().tokenValue
    }
}