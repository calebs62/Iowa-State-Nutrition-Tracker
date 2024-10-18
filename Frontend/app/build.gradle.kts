plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.a1_jubair_6_frontend"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.a1_jubair_6_frontend"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    implementation(libs.glide)
    implementation(libs.gson)
    implementation(libs.androidx.junit)
    implementation(libs.okhttp)
    testImplementation(libs.junit.junit)
    testImplementation(libs.testng)
}