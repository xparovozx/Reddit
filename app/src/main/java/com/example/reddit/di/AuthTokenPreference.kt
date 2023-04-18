package com.example.reddit.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.reddit.oauth_data.AuthConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenPreference @Inject constructor(@ApplicationContext context : Context) {

    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
    private  val prefs =  EncryptedSharedPreferences.create(
        "shared_preferences_filename.txt",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getStoredData () : String {
        return prefs.getString(AuthConfig.ACCESS_PREF_KEY, "")?: ""
    }

    fun setStoredData(accessToken : String) {
        prefs.edit()
            .putString(AuthConfig.ACCESS_PREF_KEY, accessToken)
            .apply()
    }
}