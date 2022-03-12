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
import com.yoxjames.openstitch.loading.ViewScreen
import com.yoxjames.openstitch.navigation.HotPatterns
import com.yoxjames.openstitch.navigation.NavigationScreenState
import com.yoxjames.openstitch.navigation.NavigationState
import com.yoxjames.openstitch.navigation.NavigationStateFunction
import com.yoxjames.openstitch.navigation.NavigationTransition
import com.yoxjames.openstitch.navigation.None
import com.yoxjames.openstitch.oauth.OpenStitchAuthenticator
import com.yoxjames.openstitch.pattern.api.PatternApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import java.io.File
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
    fun providePatternApiService(retrofit: Retrofit): PatternApiService {
        return retrofit.create(PatternApiService::class.java)
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
