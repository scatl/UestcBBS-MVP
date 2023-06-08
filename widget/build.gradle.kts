plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    defaultConfig {
        minSdk      =    BuildVersion.minSdk
        targetSdk   =    BuildVersion.targetSdk
        compileSdk  =    BuildVersion.compileSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        viewBinding = true
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
    implementation(project(":util"))
    implementation(Dependencies.material)
    implementation(Dependencies.okhttp)
    implementation(Dependencies.glide)
    implementation(Dependencies.slidingUpPanel)
    implementation(Dependencies.subsamplingImageview)
    implementation(Dependencies.lottie)
    Dependencies.androidX.forEach { implementation(it) }
    Dependencies.immersionbar.forEach { implementation(it) }
    Dependencies.dkplayer.forEach { implementation(it) }
    Dependencies.refresh.forEach{ implementation(it) }

    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.extJunit)
    androidTestImplementation(Dependencies.espressoCore)
}