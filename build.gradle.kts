// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Dependencies.gradleVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.kotlinVersion}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Dependencies.daggerVersion}")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xuse-experimental=androidx.compose.animation.ExperimentalAnimationApi",
                "-Xuse-experimental=androidx.compose.ExperimentalComposeApi",
                "-Xuse-experimental=androidx.compose.material.ExperimentalMaterialApi",
                "-Xuse-experimental=androidx.compose.runtime.ExperimentalComposeApi",
                "-Xuse-experimental=androidx.compose.ui.ExperimentalComposeUiApi",
                "-Xuse-experimental=androidx.compose.foundation.ExperimentalFoundationApi"
            )
        }
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}