plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.service)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serializable)
    id("kotlin-parcelize")
}

android {
    namespace = "com.buddhatutors"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.buddhatutors"
        minSdk = 24
        targetSdk = 34
        versionCode = 7
        versionName = "0.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
            signingConfig = signingConfigs.getByName("debug")
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
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.navigation.hilt)
    implementation(libs.firebase.config.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    //implementation(libs.firebase.crashlytics)

    implementation(libs.kotlinx.serialization.json)

    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.datastore)

    implementation(libs.gson)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)
    ksp(libs.moshi.kotlin.codegen)

    implementation(project(":common"))

    implementation(project(":core:domain"))
    implementation(project(":core:data"))

    implementation(project(":core:auth:domain"))
    implementation(project(":core:auth:data"))

    implementation(project(":feature:login"))
    implementation(project(":feature:registration"))
    implementation(project(":feature:forgotpassword"))
    implementation(project(":feature:termconditions"))
    implementation(project(":feature:userprofile"))

    implementation(project(":feature:admin:admin-main"))
    implementation(project(":feature:admin:master-home"))
    implementation(project(":feature:admin:add-tutor"))
    implementation(project(":feature:admin:add-master-tutor"))
    implementation(project(":feature:admin:add-topic"))
    implementation(project(":feature:admin:tutor-detail-verification"))

    implementation(project(":feature:tutor:home"))
    implementation(project(":feature:tutor:edit-tutor"))

    implementation(project(":feature:student:home"))
    implementation(project(":feature:student:slot-booking"))
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
