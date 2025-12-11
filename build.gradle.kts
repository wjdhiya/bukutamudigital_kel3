// build.gradle.kts (Project/Root Level)

plugins {
    // Memastikan plugin Android dan Kotlin ada
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // Mengaktifkan plugin Google Services (tanpa apply, karena ini di level root)
    alias(libs.plugins.googleGmsGoogleServices) apply false
}