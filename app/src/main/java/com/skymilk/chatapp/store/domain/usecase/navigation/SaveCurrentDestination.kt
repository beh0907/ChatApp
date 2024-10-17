package com.skymilk.chatapp.store.domain.usecase.navigation

import com.skymilk.chatapp.store.domain.model.NavigationState
import com.skymilk.chatapp.store.domain.repository.NavigationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveCurrentDestination(
    private val navigationRepository: NavigationRepository
) {
    suspend operator fun invoke(navigationState: NavigationState) {
        return navigationRepository.saveCurrentDestination(navigationState)
    }
}
