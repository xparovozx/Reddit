package com.example.reddit.ui.fragments.login

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reddit.R
import com.example.reddit.oauth_data.AuthRepository
import com.example.reddit.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import net.openid.appauth.AuthorizationException
import net.openid.appauth.TokenRequest
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val authService = authRepository.getAuthService()
    private val openAuthPageLiveEvent = SingleLiveEvent<Intent>()
    private val toastLiveEvent = SingleLiveEvent<Int>()
    private val loadingMutableLiveData = MutableLiveData(false)
    private val authSuccessLiveEvent = SingleLiveEvent<Unit>()

    val openAuthPageLiveData: LiveData<Intent>
        get() = openAuthPageLiveEvent
    val loadingLiveData: LiveData<Boolean>
        get() = loadingMutableLiveData
    val toastLiveData: LiveData<Int>
        get() = toastLiveEvent
    val authSuccessLiveData: LiveData<Unit>
        get() = authSuccessLiveEvent

    override fun onCleared() {
        super.onCleared()
        authRepository.authServiceDispose()
    }

    fun onAuthCodeFailed(exception: AuthorizationException) {
        toastLiveEvent.postValue(R.string.auth_canceled)
    }

    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        loadingMutableLiveData.postValue(true)
        Timber.tag("TokenStatus").d("token request onAuthCodeReceived VM = $tokenRequest")
        authRepository.performTokenRequest(
            tokenRequest = tokenRequest,
            onComplete = {
                loadingMutableLiveData.postValue(false)
                authSuccessLiveEvent.postValue(Unit)
            },
            onError = {
                loadingMutableLiveData.postValue(false)
                toastLiveEvent.postValue(R.string.auth_canceled)
            },
            authService = authService
        )
    }

    fun openLoginPage() {
        val customTabsIntent = authRepository.getCustomTabsIntent()
        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRepository.getAuthRequest(),
            customTabsIntent
        )
        openAuthPageLiveEvent.postValue(openAuthPageIntent)
    }
}

