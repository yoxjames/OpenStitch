package com.yoxjames.openstitch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.yoxjames.openstitch.navigation.Back
import com.yoxjames.openstitch.navigation.HotPatterns
import com.yoxjames.openstitch.navigation.NavigationScreenState
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.navigation.NavigationTransition
import com.yoxjames.openstitch.navigation.None
import com.yoxjames.openstitch.navigation.PatternDetail
import com.yoxjames.openstitch.navigation.SearchingPatterns
import com.yoxjames.openstitch.oauth.AuthenticationManager
import com.yoxjames.openstitch.pattern.vm.PatternDetailViewModel
import com.yoxjames.openstitch.pattern.vm.PatternListScreenDataSource
import com.yoxjames.openstitch.pattern.vm.PatternListView
import com.yoxjames.openstitch.ui.theme.OpenStitchTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagerApi
@AndroidEntryPoint
class OpenStitchActivity : ComponentActivity() {
    @Inject lateinit var authenticationManager: AuthenticationManager
    @Inject lateinit var navigationScreenState: Flow<@JvmSuppressWildcards NavigationScreenState>
    @Inject lateinit var navigationState: StateFlow<@JvmSuppressWildcards NavigationState>
    @Inject lateinit var patternDetailViewModel: PatternDetailViewModel
    @Inject lateinit var patternListScreenDataSource: PatternListScreenDataSource
    @Inject lateinit var navigationTransitions: MutableSharedFlow<@JvmSuppressWildcards NavigationTransition>
    @Inject lateinit var navigationBus: MutableSharedFlow<@JvmSuppressWildcards NavigationTransition>

    override fun onBackPressed() {
        if (navigationState.value.isBackAvailable) {
            lifecycleScope.launch { navigationBus.emit(Back) }
        } else {
            super.onBackPressed()
        }
    }

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
                when (navigationScreenState.collectAsState(HotPatterns).value) {
                    None -> Unit
                    HotPatterns -> PatternListView(
                        navigationTransitions = navigationTransitions,
                        patternListScreenDataSource = patternListScreenDataSource
                    )
                    is PatternDetail -> patternDetailViewModel.ComposeViewModel()
                    is SearchingPatterns -> PatternListView(
                        navigationTransitions = navigationTransitions,
                        patternListScreenDataSource = patternListScreenDataSource
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        authenticationManager.onActivityResult(requestCode, data)
        attachUi()
        super.onActivityResult(requestCode, resultCode, data)
    }
}
