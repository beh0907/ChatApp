package com.skymilk.chatapp.di

import com.skymilk.chatapp.store.domain.repository.AuthRepository
import com.skymilk.chatapp.store.domain.usecase.AuthUseCases
import com.skymilk.chatapp.store.domain.usecase.GetCurrentUser
import com.skymilk.chatapp.store.domain.usecase.SignInWithEmailAndPassword
import com.skymilk.chatapp.store.domain.usecase.SignInWithGoogle
import com.skymilk.chatapp.store.domain.usecase.SignOut
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideSignInWithGoogleUseCase(authRepository: AuthRepository): AuthUseCases =
        AuthUseCases(
            SignInWithGoogle(authRepository),
            SignInWithEmailAndPassword(authRepository),
            SignOut(authRepository),
            GetCurrentUser(authRepository)
        )
}