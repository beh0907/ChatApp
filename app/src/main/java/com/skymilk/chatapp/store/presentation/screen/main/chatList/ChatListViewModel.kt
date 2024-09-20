package com.skymilk.chatapp.store.presentation.screen.main.chatList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class ChatListViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    private val chatUseCases: ChatUseCases
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(userId: String): ChatListViewModel
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

    init {
//        createChatRoom("채팅방 ㅎㅎㅎ", listOf("HpgwjfK6feTd5Ut0P4rn2stpgOf1", "LekzbyeX6eQMODyECMYKQVphIYF3"))
    }

//    val chatRooms = chatUseCases.getChatRooms(userId)
//            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val chatRooms = chatUseCases.getChatRooms(userId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun createChatRoom(name: String, participants: List<String>) {
        viewModelScope.launch {
            chatUseCases.createChatRoom(name, participants)
        }
    }
}

