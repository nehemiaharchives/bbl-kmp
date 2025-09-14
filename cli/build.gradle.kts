plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    macosX64() // intel mac
    //macosArm64() // m1/2/3/4 mac
    linuxX64()
    //mingwX64() // windows, good to have, and build later, but for now commenting out
    jvm() // primarily for testing purposes,
    // in case windows native implementation has too much problems
    // in the future used for desktop app

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.shared)
                implementation(libs.kotlinx.coroutines)
            }
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries {
            executable {
                entryPoint = "org.gnit.bible.cli.main"
            }
        }
    }
}

tasks.register("buildNativeRelease") {
    dependsOn(
        "linkReleaseExecutableMacosX64",
        "linkReleaseExecutableMacosArm64",
        "linkReleaseExecutableLinuxX64",
        "linkReleaseExecutableMingwX64"
    )
}
