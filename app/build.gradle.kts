plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.sousuo.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sousuo.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "0.3.0"
    }
}
