// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("org.sonatype.gradle.plugins.scan") version "3.1.1"
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
    id("org.sonarqube") version "6.2.0.5505"
    id("androidx.navigation.safeargs.kotlin") version "2.8.3" apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
}

sonar {
    properties {
        property("sonar.projectKey", "Team-2-SA60_KakiGoWhere-Android")
        property("sonar.organization", "team2sa60")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
