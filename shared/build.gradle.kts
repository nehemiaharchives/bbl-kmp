import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {

    // mobile app
    // bbl-kmp-android
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // bbl-kmp-ios
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    // bbl-kmp-cli
    macosX64() // intel mac
    macosArm64() // m1/2/3/4 mac
    linuxX64()
    mingwX64()

    // desktop app (and windows jvm cli if windows native development has too much problem)
    jvm()
    
    sourceSets {
        val commonMain by getting
        val commonTest by getting

        // New aggregated native source set so we can place shared native code in src/nativeMain
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        // Optional aggregated test source set if needed later (src/nativeTest)
        val nativeTest by creating {
            dependsOn(commonTest)
        }

        // Make every native target's main source set depend on nativeMain
        val iosArm64Main by getting { dependsOn(nativeMain) }
        val iosSimulatorArm64Main by getting { dependsOn(nativeMain) }
        val iosX64Main by getting { dependsOn(nativeMain) }
        val macosX64Main by getting { dependsOn(nativeMain) }
        val macosArm64Main by getting { dependsOn(nativeMain) }
        // introduce linuxMain aggregate so :cli:linuxMain can resolve :shared:linuxMain metadata
        val linuxMain by creating { dependsOn(nativeMain) }
        val linuxX64Main by getting { dependsOn(linuxMain) }
        val mingwX64Main by getting { dependsOn(nativeMain) }

        // (Tests) hook them to nativeTest if/when created
        val iosArm64Test by getting { dependsOn(nativeTest) }
        val iosSimulatorArm64Test by getting { dependsOn(nativeTest) }
        val iosX64Test by getting { dependsOn(nativeTest) }
        val macosX64Test by getting { dependsOn(nativeTest) }
        val macosArm64Test by getting { dependsOn(nativeTest) }
        val linuxX64Test by getting { dependsOn(nativeTest) }
        val mingwX64Test by getting { dependsOn(nativeTest) }

        commonMain.dependencies {
            // put your Multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }


}

android {
    namespace = "org.gnit.bible.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
