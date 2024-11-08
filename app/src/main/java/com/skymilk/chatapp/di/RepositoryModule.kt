package com.skymilk.chatapp.di

import com.skymilk.chatapp.store.data.repository.AuthRepositoryImpl
import com.skymilk.chatapp.store.data.repository.ChatRepositoryImpl
import com.skymilk.chatapp.store.data.repository.SharedRepositoryImpl
import com.skymilk.chatapp.store.data.repository.ChatRoomSettingRepositoryImpl
import com.skymilk.chatapp.store.data.repository.StorageRepositoryImpl
import com.skymilk.chatapp.store.data.repository.UserRepositoryImpl
import com.skymilk.chatapp.store.data.repository.UserSettingRepositoryImpl
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import com.skymilk.chatapp.store.domain.repository.SharedRepository
import com.skymilk.chatapp.store.domain.repository.ChatRoomSettingRepository
import com.skymilk.chatapp.store.domain.repository.StorageRepository
import com.skymilk.chatapp.store.domain.repository.UserRepository
import com.skymilk.chatapp.store.domain.repository.UserSettingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    //@Binds는 반환 타입이 interface
    //파라미터는 interface의 구현체 클래스로 활용
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindStorageRepository(impl: StorageRepositoryImpl): StorageRepository

    @Binds
    @Singleton
    abstract fun bindChatRoomSettingRepository(impl: ChatRoomSettingRepositoryImpl): ChatRoomSettingRepository

    @Binds
    @Singleton
    abstract fun bindUserSettingRepository(impl: UserSettingRepositoryImpl): UserSettingRepository

    @Binds
    @Singleton
    abstract fun bindSharedRepository(impl: SharedRepositoryImpl): SharedRepository
}