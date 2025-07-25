// File: app/build.gradle.kts
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.gradle.api.artifacts.dsl.DependencyHandler

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.lombok)
    // Эти строки возвращены к стандартному виду (через alias),
    // предполагая, что они уже определены в libs.versions.toml
    alias(libs.plugins.kotlin.serialization)
    // Если вам нужен kapt для других частей проекта, оставьте: alias(libs.plugins.kotlin.kapt)
    // Если он был добавлен ТОЛЬКО для спуфинга, то его здесь не должно быть.
}

android {
    namespace = "com.retrivedmods.wclient"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.retrivedmods.wclient"
        minSdk = 28

        targetSdk = 35
        versionCode = 9
        versionName = "9.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += setOf("arm64-v8a", "armeabi-v7a")
        }
    }
    signingConfigs {
        create("shared") {
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true

            storeFile = rootDir.resolve("buildKey.jks")
            keyAlias = "UntrustedKey"
            storePassword = "123456"
            keyPassword = "123456"
        }
    }
    packaging {
        jniLibs.useLegacyPackaging = true
        resources.excludes.addAll(
            setOf(
                "DebugProbesKt.bin"
            )
        )
        resources.pickFirsts.addAll(
            setOf(
                "META-INF/INDEX.LIST",
                "META-INF/io.netty.versions.properties",
                "META-INF/DEPENDENCIES"
            )
        )
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            signingConfig = signingConfigs.getByName("shared")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
            signingConfig = signingConfigs.getByName("shared")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeCompiler {
        includeTraceMarkers = false
        includeSourceInformation = false
        generateFunctionKeyMetaClasses = false
        featureFlags = setOf(
            ComposeFeatureFlag.OptimizeNonSkippingGroups,
            ComposeFeatureFlag.PausableComposition
        )
    }
}

configurations.all {

}

fun DependencyHandler.implementationRelay() {
    debugImplementation(platform(libs.log4j.bom))
    debugImplementation(libs.log4j.api)
    debugImplementation(libs.log4j.core)
    implementation(files("libs/MuCuteRelay.jar"))
    implementation(libs.bundles.netty)
    implementation(libs.expiringmap)
    implementation(libs.network.common) // Или libs.bundles.network.common, в зависимости от вашего файла libs.versions.toml
    implementation(platform(libs.fastutil.bom))
    implementation(libs.fastutil.long.common)
    implementation(libs.fastutil.long.obj.maps)
    implementation(libs.fastutil.int.obj.maps)
    implementation(libs.fastutil.obj.int.maps)
    implementation(libs.jose4j)
    implementation(libs.math)
    implementation(libs.nbt)
    implementation(libs.snappy)
    implementation(libs.guava)
    implementation(libs.gson)
    implementation(libs.http.client)
    implementation(libs.bcprov)
    implementation(libs.okhttp)
}

dependencies {
    implementationRelay()
    implementation(libs.kotlinx.serialization.json.jvm)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // УДАЛИТЕ ЭТИ ЗАВИСИМОСТИ, если они были добавлены ТОЛЬКО для модуля спуфинга:
    // implementation("com.auth0:java-jwt:4.4.0")
    // implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    // implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
    // implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")

    // УДАЛИТЕ ЭТУ ЗАВИСИМОСТЬ, если Lombok был добавлен ТОЛЬКО для модуля спуфинга:
    // kapt("org.projectlombok:lombok:1.18.30")
}
