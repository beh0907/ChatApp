import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.hilt.android)
    kotlin("kapt")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "1.9.22"
    alias(libs.plugins.compose.compiler)
}

//kotlin 2.0.0으로 버전업 하면서 사용불가
//val properties = Properties()
//properties.load(FileInputStream(rootProject.file("local.properties")))

// local.properties 사용
val localProperties = rootProject.file("local.properties").inputStream().use {
    Properties().apply { load(it) }
}

android {
    namespace = "com.skymilk.chatapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.skymilk.chatapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }


        buildConfigField(
            "String",
            "GOOGLE_AUTH_WEB_CLIENT_ID",
            localProperties.getProperty("google.auth.web.client.id")
        )

        buildConfigField(
            "String",
            "FIREBASE_DATABASE_URL",
            localProperties.getProperty("firebase.database.url")
        )

        buildConfigField(
            "String",
            "FIREBASE_ADMIN_KEY",
            localProperties.getProperty("firebase.admin.key")
        )

        buildConfigField(
            "String",
            "KAKAO_SDK_NATIVE_KEY",
            localProperties.getProperty("kakao.sdk.native.key")
        )

        resValue("string", "KAKAO_SDK_OAUTH_SCHEME", localProperties.getProperty("kakao.sdk.oauth.scheme"))
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
        buildConfig = true
        dataBinding = true
    }
    composeCompiler {
        enableStrongSkippingMode = true
        includeSourceInformation = true
        // composeCompiler 블록내의 설정들은 하단 Reference를 참고해보세요
        // Compose compiler -> Compose compiler options dsl
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/public-suffix-list.txt"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //serialization
    implementation(libs.kotlinx.serialization.json)

    //ConstraintLayout
    implementation(libs.androidx.constraintlayout.compose)

    //Ted
    implementation(libs.tedpermission.coroutine) // permissions
    implementation(libs.tedimagepicker) // ImagePicker

    // Coil Image
    implementation(libs.coil)
    implementation(libs.coil.transformations)

    //Image Cropper
    implementation(libs.easycrop)

    //Image Viewer
    implementation(libs.image.viewer)

    //lottie
    implementation(libs.lottie.compose)


    // Material Icon Extended
    implementation(libs.androidx.material.icons.extended)

    // lifecycle (생명주기 연동 flow 수집)
    implementation(libs.lifecycle.runtime.compose)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)

    //size
    implementation(libs.androidx.material3.window.size)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)

    //google oauth2
    implementation(libs.google.auth.library.oauth2.http)

    //Kakao Login
    implementation(libs.kakao.sdk.user)

    //Dagger Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.logging.interceptor)

    //Datastore
    implementation(libs.androidx.datastore.preferences)
}