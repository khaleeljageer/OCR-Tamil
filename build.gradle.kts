buildscript {
    dependencies {
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.gmsGoogleService) apply false
    alias(libs.plugins.ksp) apply false
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}