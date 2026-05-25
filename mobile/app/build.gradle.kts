plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.printit.mobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.printit.mobile"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val printItApiBaseUrl = providers.gradleProperty("PRINTIT_API_BASE_URL")
            .orElse("http://192.168.254.107:8080/")
            .get()
        val printItOAuthBaseUrl = providers.gradleProperty("PRINTIT_OAUTH_BASE_URL")
            .orElse(printItApiBaseUrl)
            .get()

        buildConfigField("String", "PRINTIT_API_BASE_URL", "\"$printItApiBaseUrl\"")
        buildConfigField("String", "PRINTIT_OAUTH_BASE_URL", "\"$printItOAuthBaseUrl\"")
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
