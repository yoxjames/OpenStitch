package com.yoxjames.openstitch.oauth

import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ClientAuthentication
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class OpenStitchAuthenticator @Inject constructor(
    private val authStateManager: AuthStateManager,
    private val authorizationService: AuthorizationService,
    private val clientAuthentication: ClientAuthentication
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request {
        return runBlocking {
            val authState = authStateManager.readAuthState()
            suspendCoroutine { cont ->
                authState.performActionWithFreshTokens(authorizationService, clientAuthentication) { accessToken, idToken, ex ->
                    authStateManager.writeAuthState(authState)
                    if (accessToken != null) {
                        cont.resume(
                            response.request.newBuilder()
                                .header("Authorization", "Bearer $accessToken")
                                .build()
                        )
                    } else {
                        cont.resumeWithException(ex!!.fillInStackTrace())
                    }
                }
            }
        }
    }
}
