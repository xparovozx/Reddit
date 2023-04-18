package com.example.reddit.oauth_data

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.example.reddit.di.AuthTokenPreference
import net.openid.appauth.*
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
    private val authTokenPreference: AuthTokenPreference
) : AuthRepository {

    private val authState = AuthState(AuthorizationServiceConfiguration(
        Uri.parse(AuthConfig.OAUTH_URL),
        Uri.parse(AuthConfig.TOKEN_URL)
    ))
    override fun getAuthRequest(): AuthorizationRequest {

        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.OAUTH_URL),
            Uri.parse(AuthConfig.TOKEN_URL)
        )

        val redirectUri = Uri.parse(AuthConfig.REDIRECT_URL)

        val authRequest = AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri
        )
            .setScope(AuthConfig.OAUTH_SCOPE)
            .build()
        return authRequest
    }

    override fun getAuthService(): AuthorizationService {
        val authService = AuthorizationService(context)
        Timber.tag("ClientAuth").d("auth service = $authService")
        return authService
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
        onComplete: () -> Unit,
        onError: () -> Unit
    ) {
        authService.performTokenRequest(
            tokenRequest,
            getClientAuthentication()
        ) { response, _ ->
            Timber.tag("TokenStatus").d("response = ${response.toString()}")
            when {
                (response != null) -> {

                    val accessToken = response.accessToken.orEmpty()
                    Timber.tag("AccessToken").d("access token = ${response.jsonSerializeString()}")
                    authTokenPreference.setStoredData(accessToken)
                    onComplete()
                }
                else -> onError()
            }
        }
    }

    override fun getRefreshRequest(): TokenRequest {
        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.OAUTH_URL),
            Uri.parse(AuthConfig.TOKEN_URL)
        )

        val redirectUri = Uri.parse(AuthConfig.REDIRECT_URL)
        val durationParam = mapOf("duration" to AuthConfig.DURATION)
        return TokenRequest.Builder(
            serviceConfiguration, AuthConfig.CLIENT_ID
        )
            .setRedirectUri(redirectUri)
            .setGrantType(AuthConfig.GRANT_TYPE_REFRESH)
            .setScope(AuthConfig.OAUTH_SCOPE)
            .setAdditionalParameters(durationParam)
            .build()
    }

    override fun authServiceDispose() {
        AuthorizationService(context).dispose()
    }

    override fun getCustomTabsIntent(): CustomTabsIntent {
        return CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(context, R.color.browser_actions_title_color))
            .build()
    }

    private fun getClientAuthentication(): ClientAuthentication {
        val clientAuthentication =
            ClientSecretBasic(AuthConfig.CLIENT_SECRET)
        Timber.tag("ClientAuth").d("client auth = $clientAuthentication")
        return clientAuthentication
    }
}
