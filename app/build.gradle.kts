import Dependencies.accompanistVersion
import Dependencies.activityComposeVersion
import Dependencies.appAuthVersion
import Dependencies.appCompatVersion
import Dependencies.arrowVersion
import Dependencies.chuckerVersion
import Dependencies.composeVersion
import Dependencies.coreKtxVersion
import Dependencies.daggerVersion
import Dependencies.jUnitVersion
import Dependencies.kotlinCoroutinesVersion
import Dependencies.kotlinSerializationVersion
import Dependencies.landscapistVersion
import Dependencies.lifecycleVersion
import Dependencies.materialVersion
import Dependencies.okHttpVersion
import Dependencies.retrofitKotlinSerializationConverterVersion
import Dependencies.retrofitVersion
import Dependencies.timberVersion

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.plugin.serialization") version (Dependencies.kotlinVersion)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("org.jlleitschuh.gradle.ktlint") version(Dependencies.ktLintVersion)
    id("org.jlleitschuh.gradle.ktlint-idea") version(Dependencies.ktLintVersion)
}

val CLIENT_SECRET: String by project
val CLIENT_KEY: String by project
val CLIENT_REDIRECT_URL: String by project

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.openstitch"
        minSdk = 23
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "CLIENT_SECRET", CLIENT_SECRET)
        buildConfigField("String", "CLIENT_KEY", CLIENT_KEY)
        buildConfigField("String", "CLIENT_REDIRECT_URL", CLIENT_REDIRECT_URL)
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        create("alpha") {
            initWith(getByName("debug"))
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-graphics:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinCoroutinesVersion")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:$retrofitKotlinSerializationConverterVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
    implementation("net.openid:appauth:$appAuthVersion")
    implementation("com.google.dagger:dagger:$daggerVersion")
    implementation("com.google.dagger:hilt-android:$daggerVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")
    implementation("com.jakewharton.timber:timber:$timberVersion")
    implementation("com.github.skydoves:landscapist-glide:$landscapistVersion")
    implementation("com.google.accompanist:accompanist-placeholder:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    debugImplementation("com.github.chuckerteam.chucker:library:$chuckerVersion")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:$chuckerVersion")

    kapt("com.google.dagger:hilt-compiler:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    testImplementation("junit:junit:$jUnitVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
}
