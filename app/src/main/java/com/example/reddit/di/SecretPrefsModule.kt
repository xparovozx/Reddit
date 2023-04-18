package com.example.reddit.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SecretPrefsModule {

    @Provides
    @Singleton
    fun provideMasterKeyAlias () : String {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        return MasterKeys.getOrCreate(keyGenParameterSpec)
    }

    @Provides
    @Singleton
    @SecretPrefsQualifier
    fun provideSecretPrefs(@ApplicationContext context: Context, masterKeyAlias: String) : SharedPreferences {
        return  EncryptedSharedPreferences.create(
            "shared_preferences_filename.txt",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}