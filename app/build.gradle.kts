plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // 💡 PERBAIKAN: Mengganti kotlin("parcelize") dengan notasi ID string yang lebih eksplisit
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.bukutamudigital"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bukutamudigital"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // ❗ Gunakan versi manual yang stabil dan kompatibel dengan compileSdk 34
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    // Dependencies untuk Parcelize (biasanya tidak wajib jika plugin aktif, tapi amannya ditambahkan)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.cardview:cardview:1.0.0")

    // Versi-versi di bawah ini sudah OK, tidak perlu diubah
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Dependencies untuk testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}