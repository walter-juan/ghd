import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times in each subproject's classloader
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlin.plugin.serialization).apply(false)
    alias(libs.plugins.kapt).apply(false)
    alias(libs.plugins.compose.jetbrains).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.gradleVersionUpdates)
    alias(libs.plugins.gradleVersionCatalogUpdates)
}

versionCatalogUpdate {
    sortByKey = false
}

tasks.withType<DependencyUpdatesTask> {
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }

    rejectVersionIf {
        isNonStable(candidate.version)
    }

    checkForGradleUpdate = true
    gradleReleaseChannel = "current"
}