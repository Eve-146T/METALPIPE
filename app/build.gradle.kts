plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "METAL.PIPE"
    compileSdk = 34

    defaultConfig {
        applicationId = "METAL.PIPE"
        minSdk = 21
        targetSdk = 34
        versionCode = 7874
        versionName = "7,874g/cm^3"
    }
    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {


}