package com.yoxjames.openstitch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.yoxjames.openstitch.core.ConnectableFlowHolder
import com.yoxjames.openstitch.core.ViewEventFlowAdapter
import com.yoxjames.openstitch.list.PositionalListViewEvent
import com.yoxjames.openstitch.list.StatefulListViewEvent
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.oauth.AuthenticationManager
import com.yoxjames.openstitch.ui.core.BackPushed
import com.yoxjames.openstitch.ui.core.LoadingViewState
import com.yoxjames.openstitch.ui.core.ScreenStates
import com.yoxjames.openstitch.ui.core.ScreenViewEvent
import com.yoxjames.openstitch.ui.core.ScreenViewState
import com.yoxjames.openstitch.ui.core.ViewScreen
import com.yoxjames.openstitch.ui.theme.OpenStitchTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OpenStitchActivity : ComponentActivity() {
    @Inject lateinit var authenticationManager: AuthenticationManager
    @Inject lateinit var viewEventFlowAdapter: ViewEventFlowAdapter<@JvmSuppressWildcards ScreenViewEvent>
    @Inject lateinit var screenViewStates: Flow<@JvmSuppressWildcards ScreenViewState>
    @Inject lateinit var connectableFlowHolder: ConnectableFlowHolder<@JvmSuppressWildcards StatefulListViewEvent>
    @Inject lateinit var appState: StateFlow<@JvmSuppressWildcards OpenStitchState>
    @Inject lateinit var navigationState: StateFlow<@JvmSuppressWildcards NavigationState>

    override fun onBackPressed() {
        if (navigationState.value.isBackAvailable) {
            lifecycleScope.launch { viewEventFlowAdapter.onEvent(BackPushed) }
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch { viewEventFlowAdapter.onEvent(ViewScreen) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!authenticationManager.isAuthenticated) {
            authenticationManager.authenticateRavelry()
        } else {
            attachUi()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            connectableFlowHolder.connectFlow(
                viewEventFlowAdapter.flow
                    .filterIsInstance<PositionalListViewEvent>()
                    .mapNotNull {
                        when (val listState = appState.value) {
                            is DetailScreenState -> null
                            is ListScreenState -> StatefulListViewEvent(viewEvent = it.event, state = listState.listState.items[it.pos])
                            LoadingScreenState -> null
                        }
                    }
            )
        }
    }

    private fun attachUi() {
        setContent {
            OpenStitchTheme {
                val patternListState = rememberLazyListState()
                val viewState = screenViewStates.collectAsState(initial = LoadingViewState)
                viewState.value.Composable(ScreenStates(patternListState), viewEventFlowAdapter)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        authenticationManager.onActivityResult(requestCode, data)
        attachUi()
        super.onActivityResult(requestCode, resultCode, data)
    }
}
