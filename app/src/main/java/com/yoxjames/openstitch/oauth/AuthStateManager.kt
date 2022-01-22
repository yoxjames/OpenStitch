package com.yoxjames.openstitch.oauth

import android.content.Context
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

import net.openid.appauth.AuthState
import javax.inject.Inject

@ActivityScoped
class AuthStateManager @Inject constructor(
    @ActivityContext private val context: Context
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