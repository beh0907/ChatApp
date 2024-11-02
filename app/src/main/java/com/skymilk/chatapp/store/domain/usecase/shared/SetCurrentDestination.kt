package com.skymilk.chatapp.store.domain.usecase.shared

import com.skymilk.chatapp.store.domain.model.NavigationState
import com.skymilk.chatapp.store.domain.repository.SharedRepository

class SetCurrentDestination(
    private val sharedRepository: SharedRepository
) {
    suspend operator fun invoke(navigationState: NavigationState) {
        return sharedRepository.setCurrentDestination(navigationState)
    }
}
