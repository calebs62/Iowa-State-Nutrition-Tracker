plugins {
    alias(libs.plugins.android.application)
    jacoco
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
        debug {
            enableAndroidTestCoverage = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        getByName("test") {
            java.srcDirs("src/test/java")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    tasks.withType<Test> {
        testLogging {
            outputs.upToDateWhen { false }
            showStandardStreams = true
            events("passed", "skipped", "failed")
        }
    }
}

// Add this jacoco configuration
tasks.withType<JacocoReport> {
    reports {
        xml.required.set(true)
        html.required.set(true)
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
    implementation(libs.okhttp)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.lottie)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")
    androidTestImplementation(libs.androidx.rules)

    testImplementation(libs.junit.junit)
    testImplementation(libs.mockito.android)

    androidTestImplementation(libs.androidx.espresso.core.v361)
}
