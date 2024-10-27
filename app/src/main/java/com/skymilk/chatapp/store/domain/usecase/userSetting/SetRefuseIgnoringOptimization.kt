package com.skymilk.chatapp.store.domain.usecase.userSetting

import com.skymilk.chatapp.store.domain.repository.UserSettingRepository
import kotlinx.coroutines.flow.Flow

class SetRefuseIgnoringOptimization(
    private val userSettingRepository: UserSettingRepository
) {
    suspend operator fun invoke() {
        return userSettingRepository.setRefuseIgnoringOptimization()
    }

}