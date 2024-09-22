package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class ProfileEditViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    private val userUseCases: UserUseCases
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(userId: String): ProfileEditViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            userId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(userId) as T
            }
        }
    }

    fun updateUserProfile(user:User) {
        viewModelScope.launch {
            userUseCases.updateProfile(user)
        }
    }
}