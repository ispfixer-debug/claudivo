plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.vito.client"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vito.client"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
        }
        release {
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
        freeCompilerArgs = listOf("-opt-in=kotlinx.coroutines.DebugMetadata", "-opt-in=kotlin.RequiresOptIn")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.compose.ui.tooling)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.security)
    implementation(libs.androidx.multidex)

    // Supabase (disabled - not available in Maven)
    // implementation(libs.supabase.kt)
    // implementation(libs.supabase.gotrue)
    // implementation(libs.supabase.realtime)
    // implementation(libs.supabase.storage)
    // implementation(libs.supabase.postgrest)
    // implementation(libs.supabase.functions)

    // Stripe (temporarily disabled - dependencies not in Maven)
    // implementation(libs.stripe.android)
    // implementation(libs.stripe.identity)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // Google Maps
    implementation(libs.maps.compose)
    implementation(libs.maps.services)
    implementation(libs.maps.location)
    implementation(libs.places)

    // Design System
    implementation(project(":vito_design_system"))

    // Local Core
    implementation(project(":vito_core"))

    // Image Loading
    implementation(libs.coil.compose)

    // Charts
    implementation(libs.vico.compose)

    // QR Code
    implementation(libs.zxing.android)

    // Biometric
    implementation(libs.biometric)

    // Work Manager
    implementation(libs.work.runtime)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // OkHttp
    implementation(libs.okhttp)

    // Testing
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.tooling)
}