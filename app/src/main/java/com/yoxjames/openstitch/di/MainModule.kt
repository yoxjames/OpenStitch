package com.yoxjames.openstitch.di

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yoxjames.openstitch.BuildConfig
import com.yoxjames.openstitch.DetailScreenState
import com.yoxjames.openstitch.ListScreenState
import com.yoxjames.openstitch.LoadingScreenState
import com.yoxjames.openstitch.OpenStitchState
import com.yoxjames.openstitch.core.ConnectableFlowHolder
import com.yoxjames.openstitch.core.ViewEventFlowAdapter
import com.yoxjames.openstitch.di.PatternsModule.PATTERNS_SCREEN
import com.yoxjames.openstitch.list.StatefulListViewEvent
import com.yoxjames.openstitch.navigation.Back
import com.yoxjames.openstitch.navigation.NavigationScreenState
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.navigation.NavigationStateFunction
import com.yoxjames.openstitch.navigation.NavigationTransition
import com.yoxjames.openstitch.navigation.OpenHotPatterns
import com.yoxjames.openstitch.navigation.OpenPatternDetail
import com.yoxjames.openstitch.navigation.PatternDetail
import com.yoxjames.openstitch.navigation.SearchPattern
import com.yoxjames.openstitch.oauth.OpenStitchAuthenticator
import com.yoxjames.openstitch.pattern.PatternFlowFactory
import com.yoxjames.openstitch.pattern.PatternRow
import com.yoxjames.openstitch.search.InactiveSearchState
import com.yoxjames.openstitch.search.SearchState
import com.yoxjames.openstitch.ui.SearchBackClick
import com.yoxjames.openstitch.ui.TopBarBackClick
import com.yoxjames.openstitch.ui.TopBarSearchViewEvent
import com.yoxjames.openstitch.ui.core.BackPushed
import com.yoxjames.openstitch.ui.core.ScreenViewEvent
import com.yoxjames.openstitch.ui.core.ScreenViewState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretBasic
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File
import javax.inject.Named

@FlowPreview
@ExperimentalCoroutinesApi
@Module
@InstallIn(ActivityComponent::class)
object MainModule {
    private const val RAVELRY_API_URL = "https://api.ravelry.com/"
    private const val authURL = "https://www.ravelry.com/oauth2/auth"
    private const val tokenURL = "https://www.ravelry.com/oauth2/token"

    private val json = Json { ignoreUnknownKeys = true }

    val authServiceConfig = AuthorizationServiceConfiguration(Uri.parse(authURL), Uri.parse(tokenURL))

    @ExperimentalSerializationApi
    @Provides
    @ActivityScoped
    fun provideRavelryRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(RAVELRY_API_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @ActivityScoped
    fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
        return ChuckerInterceptor.Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(250000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()
    }

    @Provides
    @ActivityScoped
    fun provideOkHttpWithAuthenticator(
        openStitchAuthenticator: OpenStitchAuthenticator,
        @ApplicationContext context: Context,
        chuckerInterceptor: ChuckerInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(chuckerInterceptor)
            .authenticator(openStitchAuthenticator)
            // TODO: I suspect this isn't actually working right... Need to dig in....
            .cache(
                Cache(
                    directory = File(context.cacheDir, "okhttp_cache"),
                    maxSize = 50L * 1024L * 1024L // 50 MiB
                )
            )
            .build()
    }

    @Provides
    fun provideAuthService(@ActivityContext context: Context): AuthorizationService {
        return AuthorizationService(context)
    }

    @Provides
    fun provideClientAuthentication(): ClientAuthentication = ClientSecretBasic(BuildConfig.CLIENT_SECRET)

    @Provides
    @ActivityScoped
    fun provideViewEventFlowAdapter(): ViewEventFlowAdapter<@JvmSuppressWildcards ScreenViewEvent> {
        return ViewEventFlowAdapter()
    }

    @Provides
    @ActivityScoped
    fun provideStatefulViewEventAdapter(): ConnectableFlowHolder<@JvmSuppressWildcards StatefulListViewEvent> {
        return ConnectableFlowHolder()
    }

    @Provides
    @ActivityScoped
    fun provideViewEventFlow(
        viewEventFlowAdapter: ViewEventFlowAdapter<@JvmSuppressWildcards ScreenViewEvent>
    ): Flow<@JvmSuppressWildcards ScreenViewEvent> {
        return viewEventFlowAdapter.flow
    }

    @Provides
    @ActivityScoped
    fun provideNavigationTransitions(
        statefulListViewEvents: Flow<@JvmSuppressWildcards StatefulListViewEvent>,
        screenViewEvents: Flow<@JvmSuppressWildcards ScreenViewEvent>, // TODO: Maybe this doesn't belong here?
        @Named(PATTERNS_SCREEN) searchStates: StateFlow<@JvmSuppressWildcards SearchState>,
    ): Flow<@JvmSuppressWildcards NavigationTransition> {
        return merge(
            statefulListViewEvents.map { it.state }
                .filterIsInstance<PatternRow>()
                .map { OpenPatternDetail(it.listPattern.id) },
            searchStates.filter { it !is InactiveSearchState }.map { SearchPattern(it.text) },
            screenViewEvents.filterIsInstance<BackPushed>().map { Back },
            screenViewEvents.filterIsInstance<TopBarSearchViewEvent>()
                .map { it.searchViewEvent }
                .filterIsInstance<SearchBackClick>().map { Back },
            screenViewEvents.filterIsInstance<TopBarBackClick>().map { Back },
            flowOf(OpenHotPatterns),
        ).flowOn(Dispatchers.IO)
    }

    @Provides
    @ActivityScoped
    fun provideNavigationStates(
        navigationTransitions: Flow<@JvmSuppressWildcards NavigationTransition>
    ): Flow<@JvmSuppressWildcards NavigationState> {
        return navigationTransitions.scan(NavigationState(emptyList())) { state, transition ->
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
        navigationStates: Flow<@JvmSuppressWildcards NavigationState>
    ): Flow<@JvmSuppressWildcards NavigationScreenState> {
        return navigationStates.map { it.navigationState }
    }

    @Provides
    @ActivityScoped
    fun provideActivityCoroutineScope(activity: Activity): CoroutineScope {
        return (activity as ComponentActivity).lifecycleScope
    }

    @Provides
    @ActivityScoped
    fun provideAppState(
        coroutineScope: CoroutineScope,
        @Named(PATTERNS_SCREEN) listScreenStates: Flow<@JvmSuppressWildcards ListScreenState>,
        patternFlowFactory: PatternFlowFactory,
        navigationStates: StateFlow<@JvmSuppressWildcards NavigationState>,
    ): StateFlow<@JvmSuppressWildcards OpenStitchState> {
        // Must be transformLatest or flatMapMerge. A new navigation state cancels the suspending call to emit on searchStates
        return merge(
            navigationStates.map { it.navigationState }
                .filterIsInstance<PatternDetail>()
                .transform { navState ->
                    emitAll(
                        patternFlowFactory.getFullPattern(navState.patternId).map {
                            DetailScreenState(
                                contentState = it,
                                loadingState = it.loadingState,
                                navigationState = navigationStates.value
                            )
                        }
                    )
                },
            listScreenStates
        ).stateIn(coroutineScope, SharingStarted.Lazily, LoadingScreenState)
    }

    @Provides
    @ActivityScoped
    fun provideAppStates(appState: StateFlow<@JvmSuppressWildcards OpenStitchState>): Flow<@JvmSuppressWildcards OpenStitchState> {
        return appState
    }

    @Provides
    @ActivityScoped
    fun provideScreenViewStates(appStates: Flow<@JvmSuppressWildcards OpenStitchState>): Flow<@JvmSuppressWildcards ScreenViewState> {
        return appStates.map { it.viewState }
    }

    @Provides
    @ActivityScoped
    fun provideStatefulViewEvents(
        connectableFlowHolder: ConnectableFlowHolder<@JvmSuppressWildcards StatefulListViewEvent>
    ): Flow<@JvmSuppressWildcards StatefulListViewEvent> {
        return connectableFlowHolder.flow
    }
}
