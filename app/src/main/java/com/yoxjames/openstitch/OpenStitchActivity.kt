package com.yoxjames.openstitch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.yoxjames.openstitch.oauth.AuthenticationManager
import com.yoxjames.openstitch.pattern.vm.NavGraphs
import com.yoxjames.openstitch.ui.theme.OpenStitchTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalMaterialApi
@AndroidEntryPoint
class OpenStitchActivity : ComponentActivity() {
    @Inject lateinit var authenticationManager: AuthenticationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!authenticationManager.isAuthenticated) {
            authenticationManager.authenticateRavelry()
        } else {
            attachUi()
        }
    }

    private fun attachUi() {
        setContent {
            OpenStitchTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        authenticationManager.onActivityResult(requestCode, data)
        attachUi()
        super.onActivityResult(requestCode, resultCode, data)
    }
}
