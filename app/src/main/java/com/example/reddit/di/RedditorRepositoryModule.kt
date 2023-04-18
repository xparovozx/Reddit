package com.example.reddit.di

import com.example.reddit.data.RedditorRepository
import com.example.reddit.data.RedditorRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RedditorRepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun provideRedditorRepository(impl: RedditorRepositoryImpl): RedditorRepository
}