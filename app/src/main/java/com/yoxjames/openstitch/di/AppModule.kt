package com.yoxjames.openstitch.di

import android.content.Context
import android.net.Uri
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yoxjames.openstitch.BuildConfig
import com.yoxjames.openstitch.oauth.OpenStitchAuthenticator
import com.yoxjames.openstitch.pattern.api.PatternApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    private const val RAVELRY_API_URL = "https://api.ravelry.com/"
    private val json = Json { ignoreUnknownKeys = true }

    @Provides
    fun provideClientAuthentication(): ClientAuthentication = ClientSecretBasic(BuildConfig.CLIENT_SECRET)

    private const val authURL = "https://www.ravelry.com/oauth2/auth"
    private const val tokenURL = "https://www.ravelry.com/oauth2/token"

    val authServiceConfig = AuthorizationServiceConfiguration(Uri.parse(authURL), Uri.parse(tokenURL))

    @Provides
    fun provideAuthService(@ApplicationContext context: Context): AuthorizationService {
        // TODO CK - Is it okay for this to be application context?
        return AuthorizationService(context)
    }

    @Provides
    fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
        return ChuckerInterceptor.Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(250000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()
    }

    @Provides
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

    @ExperimentalSerializationApi
    @Provides
    fun provideRavelryRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(RAVELRY_API_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    fun providePatternApiService(retrofit: Retrofit): PatternApiService {
        return retrofit.create(PatternApiService::class.java)
    }
}
