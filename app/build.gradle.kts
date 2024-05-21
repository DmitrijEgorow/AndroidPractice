@file:Suppress("UnstableApiUsage")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
}

android {
    defaultConfig {
        applicationId = "ru.myitschool.lab23"
        versionCode = 1
        versionName = "0.0.1"

        targetSdk = 34
        minSdk = 27
        compileSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        warningsAsErrors = true
        ignoreWarnings = false
        abortOnError = true
        checkAllWarnings = true
        lintConfig = file("lint.xml")
        lint {
            disable.addAll(
                listOf(
                    "InvalidPackage",
                    "UnusedIds",
                    "GradleDependency",
                    "UnusedResources",
                    "UnknownNullness",
                    "SyntheticAccessor",
                    "NotificationPermission",
                ),
            )
        }
    }

    applicationVariants.all {
        val lintTask = tasks["lint${name.capitalize()}"]
        assembleProvider.get().dependsOn.add(lintTask)
    }

    buildFeatures {
        viewBinding = true
    }
    namespace = "ru.myitschool.lab23"
}

dependencies {

    implementation(libs.android.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.androidx.constraintlayout)

    implementation("com.instabug.library:instabug:11.8.0")
    implementation("com.jessecorbett:diskord-bot:3.0.0")
    implementation(libs.androidx.appcompat)
    // no ktx required for navigation since https://developer.android.com/jetpack/androidx/releases/navigation#version_240_3
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Networking
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.jackson)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.converter.protobuf)
    implementation(libs.volley)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    implementation(libs.kotlinx.coroutines.android)

    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.kakao)
    androidTestImplementation(libs.androidx.test.uiautomator)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.rules)
    // only this version does not generate gradle merge problems
    androidTestImplementation(libs.androidx.test.espresso.accessibility)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(kotlin("test"))
    testImplementation(libs.androidx.test.ext)
}
