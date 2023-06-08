plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        applicationId           =       BuildVersion.applicationId
        minSdk                  =       BuildVersion.minSdk
        compileSdk              =       BuildVersion.compileSdk
        targetSdk               =       BuildVersion.targetSdk
        versionCode             =       BuildVersion.versionCode
        versionName             =       BuildVersion.versionName
        buildToolsVersion       =       BuildVersion.buildToolsVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    testImplementation(Dependencies.junit )
    androidTestImplementation(Dependencies.extJunit)
    androidTestImplementation(Dependencies.espressoCore)

    kapt(Dependencies.glidecompiler)
    implementation(project(":util"))
    implementation(project(":widget"))
    implementation(Dependencies.glide)
    implementation(Dependencies.fastjson)
    implementation(Dependencies.luban)
    implementation(Dependencies.eventbus)
    implementation(Dependencies.banner)
    implementation(Dependencies.statusBar)
    implementation(Dependencies.litepal)
    implementation(Dependencies.imageViewer)
    implementation(Dependencies.okhttp)
    implementation(Dependencies.jsoup)
    implementation(Dependencies.lottie)
    implementation(Dependencies.bugly)
    implementation(Dependencies.rxpermissions)
    implementation(Dependencies.brvah)
    implementation(Dependencies.smoothinputlayout)
    implementation(Dependencies.pictureselector)
    implementation(Dependencies.gridpager)
    implementation(Dependencies.marqueeView)
    implementation(Dependencies.shadowLayout)
    implementation(Dependencies.toasty)
    implementation(Dependencies.material)

    Dependencies.androidX.forEach { implementation (it) }
    Dependencies.retrofit2.forEach { implementation (it) }
    Dependencies.rxJava.forEach { implementation (it) }
    Dependencies.dkplayer.forEach { implementation (it) }
    Dependencies.agentWeb.forEach { implementation (it) }
    Dependencies.refresh.forEach { implementation (it) }
    Dependencies.immersionbar.forEach { implementation (it) }
}