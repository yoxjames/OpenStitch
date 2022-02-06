package com.yoxjames.openstitch.oauth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ActivityCompat.startActivityForResult
import com.yoxjames.openstitch.BuildConfig
import com.yoxjames.openstitch.di.MainModule
import dagger.hilt.android.qualifiers.ActivityContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject

class AuthenticationManager @Inject constructor(
    @ActivityContext private val context: Context,
    private val authenticationService: AuthorizationService,
    private val authStateManager: AuthStateManager,
    private val clientAuthentication: ClientAuthentication,
) {
    companion object {
        private const val PATTERN_STORE_READ = "patternstore-read"
        private const val OFFLINE = "offline"
        private const val RC_AUTH = 42069
    }

    val isAuthenticated: Boolean get() = authStateManager.readAuthState().isAuthorized

    fun authenticateRavelry() {
        val authRequest = AuthorizationRequest.Builder(
            MainModule.authServiceConfig,
            BuildConfig.CLIENT_KEY,
            ResponseTypeValues.CODE,
            Uri.parse(BuildConfig.CLIENT_REDIRECT_URL)
        ).setScope("$OFFLINE $PATTERN_STORE_READ")
            .build()
        val authIntent = authenticationService.getAuthorizationRequestIntent(authRequest)
        startActivityForResult(context as Activity, authIntent, RC_AUTH, null)
    }

    fun onActivityResult(requestCode: Int, data: Intent?) {
        val authState = authStateManager.readAuthState()
        if (requestCode == RC_AUTH && data != null) {
            val resp = AuthorizationResponse.fromIntent(data)
            val ex = AuthorizationException.fromIntent(data)
            authState.update(resp, ex)

            if (resp != null) {
                authenticationService.performTokenRequest(resp.createTokenExchangeRequest(), clientAuthentication) { resp, ex ->
                    authState.update(resp, ex)
                    if (resp != null) {
                        authStateManager.writeAuthState(authState)
                    } else {
                        println("CRITICAL FAILURE")
                    }
                }
            }
        }
    }
}