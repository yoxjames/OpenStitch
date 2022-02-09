package com.yoxjames.openstitch.di

import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.navigation.PatternDetail
import com.yoxjames.openstitch.pattern.api.PatternApiService
import com.yoxjames.openstitch.ui.core.ScreenViewEvent
import com.yoxjames.openstitch.ui.core.ViewScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
import retrofit2.Retrofit

@Module
@InstallIn(ActivityComponent::class)
@FlowPreview
@ExperimentalCoroutinesApi
object PatternModule {

    @Provides
    fun providePatternService(retrofit: Retrofit): PatternApiService {
        return retrofit.create(PatternApiService::class.java)
    }

    @Provides
    @ActivityScoped
    fun providePatternRequests(
        navigationStates: StateFlow<@JvmSuppressWildcards NavigationState>,
        screenViewEvents: Flow<@JvmSuppressWildcards ScreenViewEvent>
    ): Flow<@JvmSuppressWildcards PatternDetail> {
        val navigationRequests = navigationStates.map { it.navigationState }
            .filterIsInstance<PatternDetail>()

        val refreshes = screenViewEvents.filterIsInstance<ViewScreen>()
            .transform {
                val navState = navigationStates.value.navigationState
                if (navState is PatternDetail) emit(PatternDetail(navState.patternId))
            }

        return merge(navigationRequests, refreshes)
    }
}
