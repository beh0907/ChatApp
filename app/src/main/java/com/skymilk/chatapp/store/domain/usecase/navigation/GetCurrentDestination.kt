package com.skymilk.chatapp.store.domain.usecase.navigation

import com.skymilk.chatapp.store.domain.model.NavigationState
import com.skymilk.chatapp.store.domain.repository.NavigationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentDestination(
    private val navigationRepository: NavigationRepository
) {
    operator fun invoke() : NavigationState {
        return navigationRepository.getCurrentDestination()
    }
}