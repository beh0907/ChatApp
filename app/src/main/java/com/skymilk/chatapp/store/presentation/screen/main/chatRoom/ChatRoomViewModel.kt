package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class ChatRoomViewModel @AssistedInject constructor(
    @Assisted private val chatRoom: ChatRoomWithUsers,
    private val chatUseCases: ChatUseCases
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(chatRoom: ChatRoomWithUsers): ChatRoomViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            chatRoom: ChatRoomWithUsers
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(chatRoom) as T
            }
        }
    }

    val chatMessages = chatUseCases.getMessages(chatRoom.id)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    fun sendMessage(senderId: String, content: String) {
        viewModelScope.launch {
            chatUseCases.sendMessage(chatRoom.id, senderId, content)
        }
    }

    fun sendImageMessage(senderId: String, content: String) {
        viewModelScope.launch {
            chatUseCases.sendImageMessage(chatRoom.id, senderId, content)
        }
    }
}

