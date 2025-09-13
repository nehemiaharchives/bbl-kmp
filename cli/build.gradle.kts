plugins {
    alias(libs.plugins.kotlinJvm)
    application
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("org.gnit.bible.cli.MainKt")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.kotlinx.coroutinesSwing) // placeholder for coroutines on JVM
}
