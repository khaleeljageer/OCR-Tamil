import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.detekt.android)
    id("androidx.navigation.safeargs.kotlin")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
}

android {
    namespace = "com.jskaleel.vizhi_tamil"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jskaleel.vizhi_tamil"
        minSdk = 24
        targetSdk = 36
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

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget("17")
            freeCompilerArgs = listOf(
                "-XXLanguage:+PropertyParamAnnotationDefaultTargetMode"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    detekt {
        toolVersion = libs.versions.detekt.get()
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
        buildUponDefaultConfig = true

        source.setFrom(
            files("src/main/java", "src/main/kotlin")
        )
        parallel = false
    }
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("$rootDir/reports/detekt/detekt-report.html"))

        xml.required.set(true)
        xml.outputLocation.set(file("$rootDir/reports/detekt/detekt-report.xml"))

        txt.required.set(false)

        sarif.required.set(true)
        sarif.outputLocation.set(file("$rootDir/reports/detekt/detekt-report.sarif"))
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
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.hilt.android)
    implementation(libs.hilt.compose)
    ksp(libs.hilt.compiler)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.cz.tesseract4android.openmp)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Hilt
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Kotlinx serialization
    implementation(libs.kotlinx.serialization.json) // Latest version
    implementation(libs.lottie.compose)
    implementation(libs.coil.compose)

    implementation(libs.androidx.work.runtime.ktx)

    // ML Kit document scanner
    implementation(libs.play.services.mlkit.document.scanner)
    detektPlugins(libs.detekt.formatting)
}