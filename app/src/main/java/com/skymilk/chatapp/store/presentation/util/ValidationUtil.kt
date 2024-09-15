package com.skymilk.chatapp.store.presentation.util

import android.util.Patterns
import com.skymilk.chatapp.store.presentation.screen.auth.signUp.RegisterValidation

object ValidationUtil {
    //이메일 검증
    fun validateEmail(email: String): RegisterValidation {
        if (email.isEmpty()) return RegisterValidation.Failed("이메일을 입력해주세요.")

        //이메일 형태 여부 체크
        if (!Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) return RegisterValidation.Failed("이메일 형태로 입력해주세요.")

        return RegisterValidation.Success
    }

    //비밀번호 검증
    fun validatePassword(password: String, passwordConfirm: String): RegisterValidation {
        if (password.isEmpty()) return RegisterValidation.Failed("비밀번호를 입력해주세요.")

        if (passwordConfirm.isEmpty()) return RegisterValidation.Failed("비밀번호 확인을 입력해주세요.")

        //비밀번호 최소 자릿수 체크
        if (password.length < 6) return RegisterValidation.Failed("비밀번호를 6자 이상 입력해주세요.")

        //비밀번호 확인
        if (password != passwordConfirm) return RegisterValidation.Failed("비밀번호가 일치하지 않습니다.")

        return RegisterValidation.Success
    }
}