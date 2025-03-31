configurations.all {
    exclude(group = "org.junit.jupiter")
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.quickmart"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.quickmart"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(project(":internal:core"))
    implementation(project(":internal:data"))
    implementation(project(":internal:network"))

    androidTestImplementation("androidx.benchmark:benchmark-macro-junit4:1.2.0")

    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation(libs.compose.material.icons)
    // Navigation
    implementation(libs.navigation.compose)
    // Hilt Navigation Compose
    implementation(libs.hilt.navigation.compose)
    // Hilt
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    // Firebase
    implementation(libs.firebase.auth)
    // Serialization
    implementation(libs.serialization)
    implementation(libs.coil)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)

    testImplementation(libs.junit) // JUnit for unit tests

    // Mockk for mocking objects
    testImplementation(libs.mockk.v1125)

    // Turbine for testing StateFlow
    testImplementation(libs.turbine.v0120)

    // Coroutines testing library
    testImplementation(libs.kotlinx.coroutines.test.v164)

    // Hilt testing dependencies
    testImplementation(libs.androidx.hilt.lifecycle.viewmodel.testing)
    testImplementation(libs.hilt.android.testing)

    // Hilt testing rule
    testImplementation("androidx.test.ext:junit:1.2.1")
    api(libs.turbine)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
