package com.example.reddit.oauth_data

import com.example.reddit.di.AuthTokenPreference
import okhttp3.*
import javax.inject.Inject

class AuthTokenInterceptor @Inject constructor(private val authTokenPreference: AuthTokenPreference) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val accessToken = authTokenPreference.getStoredData()
        val modifiedRequest = original.newBuilder()
            .addHeader("User-Agent", "Reddit 1.001 by/u/VladYu  com.example.reddit")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "bearer $accessToken")
            .method(original.method, original.body)
            .build()
        return chain.proceed(modifiedRequest)
    }
}