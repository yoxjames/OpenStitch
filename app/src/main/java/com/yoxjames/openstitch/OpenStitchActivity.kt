package com.yoxjames.openstitch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.yoxjames.openstitch.loading.ViewScreen
import com.yoxjames.openstitch.navigation.Back
import com.yoxjames.openstitch.navigation.HotPatterns
import com.yoxjames.openstitch.navigation.NavigationScreenState
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.navigation.NavigationTransition
import com.yoxjames.openstitch.navigation.None
import com.yoxjames.openstitch.navigation.PatternDetail
import com.yoxjames.openstitch.navigation.SearchingPatterns
import com.yoxjames.openstitch.oauth.AuthenticationManager
import com.yoxjames.openstitch.pattern.vm.detail.PatternDetailView
import com.yoxjames.openstitch.pattern.vm.detail.PatternDetailViewModel
import com.yoxjames.openstitch.pattern.vm.list.PatternListView
import com.yoxjames.openstitch.pattern.vm.list.PatternListViewModel
import com.yoxjames.openstitch.ui.theme.OpenStitchTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalMaterialApi
@ExperimentalPagerApi
@AndroidEntryPoint
class OpenStitchActivity : ComponentActivity() {
    @Inject lateinit var authenticationManager: AuthenticationManager
    @Inject lateinit var navigationScreenState: StateFlow<@JvmSuppressWildcards NavigationScreenState>
    @Inject lateinit var navigationState: StateFlow<@JvmSuppressWildcards NavigationState>
    @Inject lateinit var patternDetailViewModel: PatternDetailViewModel
    @Inject lateinit var patternListViewModel: PatternListViewModel
    @Inject lateinit var navigationBus: MutableSharedFlow<@JvmSuppressWildcards NavigationTransition>
    @Inject lateinit var viewsBus: MutableSharedFlow<@JvmSuppressWildcards ViewScreen>

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

    override fun onResume() {
        super.onResume()
        if (!authenticationManager.isAuthenticated) {
            lifecycleScope.launch { viewsBus.emit(ViewScreen(navigationScreenState.value)) }
        }
    }

    private fun attachUi() {
        setContent {
            val listState = rememberLazyListState()
            OpenStitchTheme {
                when (navigationScreenState.collectAsState(HotPatterns).value) {
                    None -> Unit
                    HotPatterns -> PatternListView(listState, patternListViewModel)
                    is PatternDetail -> PatternDetailView(patternDetailViewModel)
                    is SearchingPatterns -> PatternListView(listState, patternListViewModel)
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
