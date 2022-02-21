package com.yoxjames.openstitch.di

import com.yoxjames.openstitch.navigation.HotPatterns
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.navigation.SearchingPatterns
import com.yoxjames.openstitch.search.DisengageSearch
import com.yoxjames.openstitch.search.InactiveSearchState
import com.yoxjames.openstitch.search.SearchConfiguration
import com.yoxjames.openstitch.search.SearchScanFunction
import com.yoxjames.openstitch.search.SearchState
import com.yoxjames.openstitch.search.SearchTransition
import com.yoxjames.openstitch.search.TopBarViewSearchViewEventTransitionMapper
import com.yoxjames.openstitch.ui.TopBarViewEvent
import com.yoxjames.openstitch.ui.core.BackPushed
import com.yoxjames.openstitch.ui.core.ScreenViewEvent
import com.yoxjames.openstitch.ui.core.ViewScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import javax.inject.Named

@Module
@InstallIn(ActivityComponent::class)
@FlowPreview
@ExperimentalCoroutinesApi
object PatternsModule {
    const val PATTERNS_SCREEN = "PATTERNS_SCREEN"

    @Provides
    @ActivityScoped
    @Named(PATTERNS_SCREEN)
    fun provideSearchStateFlow(
        viewEvents: Flow<@JvmSuppressWildcards ScreenViewEvent>,
        coroutineScope: CoroutineScope,
    ): StateFlow<@JvmSuppressWildcards SearchState> {
        val searchConfiguration = SearchConfiguration("Search Patterns")
        val topBarTransitions = viewEvents.filterIsInstance<TopBarViewEvent>()
            .transform { emitAll(TopBarViewSearchViewEventTransitionMapper(it).asFlow()) }
        val navTransitions = viewEvents.filterIsInstance<BackPushed>()
            .map { DisengageSearch }
        return merge(topBarTransitions, navTransitions)
            .scan<SearchTransition, SearchState>(
                initial = InactiveSearchState(searchConfiguration)
            ) { state, transition ->
                SearchScanFunction(state, transition)
            }.stateIn(coroutineScope, SharingStarted.Lazily, InactiveSearchState(searchConfiguration))
    }

    @Provides
    @ActivityScoped
    fun providePatternsRequests(
        navigationStates: StateFlow<@JvmSuppressWildcards NavigationState>,
        screenViewEvents: Flow<@JvmSuppressWildcards ScreenViewEvent>,
    ): Flow<@JvmSuppressWildcards HotPatterns> {
        val navigationRequests = navigationStates.map { it.navigationState }
            .filterIsInstance<HotPatterns>()
        val refreshes = screenViewEvents.filterIsInstance<ViewScreen>()
            .zip(navigationStates) { vs, ns -> Pair(vs, ns) }
            .map { it.second }
            .filterIsInstance<HotPatterns>()
            .map { HotPatterns }

        return merge(navigationRequests, refreshes)
    }

    @Provides
    @ActivityScoped
    fun provideSearchPatterns(
        @Named(PATTERNS_SCREEN) searchStates: StateFlow<@JvmSuppressWildcards SearchState>,
        navigationStates: StateFlow<@JvmSuppressWildcards NavigationState>,
        screenViewEvents: Flow<@JvmSuppressWildcards ScreenViewEvent>,
    ): Flow<@JvmSuppressWildcards SearchingPatterns> {
        val searches = searchStates.filter { it !is InactiveSearchState }
            .map { SearchingPatterns(it.text) }
        val refreshes = screenViewEvents.filterIsInstance<ViewScreen>()
            .zip(navigationStates) { vs, ns -> Pair(vs, ns) }
            .map { it.second }
            .filterIsInstance<SearchingPatterns>()
            .map { SearchingPatterns(it.searchText) }

        return merge(searches, refreshes)
    }
}
