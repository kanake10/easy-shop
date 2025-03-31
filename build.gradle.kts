// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.devtools.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.hilt.android) apply false
    id("com.google.firebase.crashlytics") version "3.0.3" apply false
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
}

subprojects {
    if (project.name != "build-logic") {
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
        apply(plugin = "com.diffplug.spotless")
        apply(plugin = "io.gitlab.arturbosch.detekt")

        spotless {
            kotlin {
                target("**/*.kt")
                licenseHeaderFile(
                    rootProject.file("${project.rootDir}/spotless/copyright.kt"),
                    "^(package|object|import|interface)"
                )
            }
        }

        ktlint {
            android.set(true)
            verbose.set(true)
            filter {
                exclude { element -> element.file.path.contains("generated/") }
            }
        }

        detekt {
            toolVersion = "1.23.6"
            config.from(rootProject.file("config/detekt.yml"))
            buildUponDefaultConfig = true
            allRules = false
            autoCorrect = true
        }
    }
}
