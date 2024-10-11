plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.proyectocompumovil.myturneraapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.proyectocompumovil.myturneraapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // TV-specific dependencies
    implementation (libs.androidx.tv.foundation.v100alpha11)
    implementation (libs.androidx.tv.material.v100)

    testImplementation (libs.junit)
    androidTestImplementation (libs.androidx.junit)
    androidTestImplementation (libs.androidx.espresso.core)
    androidTestImplementation (libs.ui.test.junit4)
    debugImplementation (libs.ui.tooling)

    // Core Compose dependencies
    implementation (libs.androidx.ui)
    implementation (platform(libs.androidx.compose.bom))
    implementation (libs.ui.tooling.preview)
    implementation (libs.androidx.material3)

    // Lifecycle and Activity support for Compose
    implementation (libs.androidx.lifecycle.runtime.ktx.v261)
    implementation (libs.androidx.activity.compose.v172)
    implementation (libs.androidx.lifecycle.viewmodel.compose)


    // Debugging and testing tools
    debugImplementation (libs.androidx.ui.tooling.v143)
    androidTestImplementation (libs.androidx.ui.test.junit4.v143)
}