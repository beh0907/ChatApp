package com.skymilk.chatapp.store.domain.usecase.userSetting

import com.skymilk.chatapp.store.domain.repository.UserSettingRepository
import kotlinx.coroutines.flow.Flow

class GetRefuseIgnoringOptimization(
    private val userSettingRepository: UserSettingRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return userSettingRepository.getRefuseIgnoringOptimization()
    }

}