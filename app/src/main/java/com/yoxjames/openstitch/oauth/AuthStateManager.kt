package com.yoxjames.openstitch.oauth

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import net.openid.appauth.AuthState

class AuthStateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun readAuthState(): AuthState {
        val authPrefs: SharedPreferences = context.getSharedPreferences("auth", MODE_PRIVATE)
        val stateJson = authPrefs.getString("stateJson", null)
        return if (stateJson != null) {
            AuthState.jsonDeserialize(stateJson)
        } else {
            AuthState()
        }
    }

    fun writeAuthState(state: AuthState) {
        val authPrefs: SharedPreferences = context.getSharedPreferences("auth", MODE_PRIVATE)
        authPrefs.edit()
            .putString("stateJson", state.jsonSerializeString())
            .apply()
    }
}
