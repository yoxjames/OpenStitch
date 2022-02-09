plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.plugin.serialization") version ("1.5.31")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("org.jlleitschuh.gradle.ktlint") version("10.2.1")
    id("org.jlleitschuh.gradle.ktlint-idea") version("10.2.1")
}

val composeVersion: String by rootProject.extra
val CLIENT_SECRET: String by project
val CLIENT_KEY: String by project
val CLIENT_REDIRECT_URL: String by project

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.example.openstitch"
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
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
        kotlinCompilerVersion = "1.6.10"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-graphics:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("net.openid:appauth:0.11.1")
    implementation("com.google.dagger:dagger:2.40.5")
    implementation("com.google.dagger:hilt-android:2.40.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.github.skydoves:landscapist-glide:1.4.5")
    implementation("com.google.accompanist:accompanist-placeholder:0.22.0-rc")
    implementation("com.google.accompanist:accompanist-pager:0.22.0-rc")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.22.0-rc")
    implementation("com.google.accompanist:accompanist-flowlayout:0.22.0-rc")
    debugImplementation("com.github.chuckerteam.chucker:library:3.5.2")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:3.5.2")

    kapt("com.google.dagger:hilt-compiler:2.40.5")
    kapt("com.google.dagger:dagger-compiler:2.40.5")
    testImplementation("junit:junit:4.+")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
}