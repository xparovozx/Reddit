package com.example.reddit.di

import com.example.reddit.oauth_data.AuthRepository
import com.example.reddit.oauth_data.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AuthModule {
    @Binds
    @ViewModelScoped
    abstract fun provideAuthRepository(impl : AuthRepositoryImpl): AuthRepository
}