package com.skymilk.chatapp.store.domain.usecase.shared

import com.skymilk.chatapp.store.domain.model.NavigationState
import com.skymilk.chatapp.store.domain.repository.SharedRepository

class GetCurrentDestination(
    private val sharedRepository: SharedRepository
) {
    operator fun invoke() : NavigationState {
        return sharedRepository.getCurrentDestination()
    }
}