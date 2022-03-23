package com.yoxjames.openstitch.di

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.yoxjames.openstitch.loading.ViewScreen
import com.yoxjames.openstitch.navigation.HotPatterns
import com.yoxjames.openstitch.navigation.NavigationScreenState
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.navigation.NavigationStateFunction
import com.yoxjames.openstitch.navigation.NavigationTransition
import com.yoxjames.openstitch.navigation.None
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

@FlowPreview
@ExperimentalCoroutinesApi
@Module
@InstallIn(ActivityComponent::class)
object MainModule {
    @Provides
    @ActivityScoped
    fun provideNavigationTransitions(): MutableSharedFlow<@JvmSuppressWildcards NavigationTransition> {
        return MutableSharedFlow()
    }

    @Provides
    @ActivityScoped
    fun provideNavigationTransitionsFlow(
        bus: MutableSharedFlow<@JvmSuppressWildcards NavigationTransition>
    ): Flow<@JvmSuppressWildcards NavigationTransition> {
        return bus.asSharedFlow()
    }

    @Provides
    @ActivityScoped
    fun provideNavigationStates(
        navigationTransitions: Flow<@JvmSuppressWildcards NavigationTransition>
    ): Flow<@JvmSuppressWildcards NavigationState> {
        return navigationTransitions.scan(NavigationState(listOf(HotPatterns))) { state, transition ->
            NavigationStateFunction(state, transition)
        }
    }

    @Provides
    @ActivityScoped
    fun provideNavigationStateFlow(
        coroutineScope: CoroutineScope,
        navigationStates: Flow<@JvmSuppressWildcards NavigationState>
    ): StateFlow<@JvmSuppressWildcards NavigationState> {
        return navigationStates.stateIn(
            coroutineScope,
            SharingStarted.Eagerly,
            NavigationState(emptyList())
        )
    }

    @Provides
    @ActivityScoped
    fun provideNavigationScreenState(
        navigationStates: StateFlow<@JvmSuppressWildcards NavigationState>,
        coroutineScope: CoroutineScope
    ): StateFlow<@JvmSuppressWildcards NavigationScreenState> {
        return navigationStates.map { it.navigationState }.stateIn(coroutineScope, SharingStarted.Eagerly, None)
    }

    @Provides
    @ActivityScoped
    fun provideActivityCoroutineScope(activity: Activity): CoroutineScope {
        return (activity as ComponentActivity).lifecycleScope
    }

    @Provides
    @ActivityScoped
    fun provideViewsBus(): MutableSharedFlow<@JvmSuppressWildcards ViewScreen> {
        return MutableSharedFlow()
    }

    @Provides
    @ActivityScoped
    fun provideViews(
        viewBus: MutableSharedFlow<@JvmSuppressWildcards ViewScreen>,
        navigationScreenState: StateFlow<@JvmSuppressWildcards NavigationScreenState>
    ): Flow<@JvmSuppressWildcards ViewScreen> {
        return merge(viewBus, navigationScreenState.map { ViewScreen(it) })
    }
}
