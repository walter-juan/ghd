import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kapt)
    alias(libs.plugins.compose)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.apollo3)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.benmanesversions)
}

group = "com.woowla"
version = "1.0.5"
val debug = (extra["debugConfig"] as String).toBoolean()

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

sqldelight {
    database("GhdDatabase") {
        packageName = "com.woowla.ghd.data.local"
    }
}

apollo {
    packageName.set("com.woowla.ghd.data.remote")
    generateOptionalOperationVariables.set(false)
}

buildConfig {
    packageName("com.woowla.ghd")
    buildConfigField("String", "APP_NAME", "\"${project.name}\"")
    buildConfigField("String", "APP_VERSION", provider { "\"${project.version}\"" })
    buildConfigField("boolean", "DEBUG", provider { "$debug" })
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    // kotlin coroutines, needed because we are using the main dispatcher
    implementation(libs.kotlinx.coroutines.swing)
    // ktor http client (needed for kamel)
    implementation(libs.ktor.client.cio)
    // GraphQL client
    implementation(libs.apollo3)
    // sqldelight database for JVM
    implementation(libs.sqldelight.sqlite.driver)
    // date time kotlin lib
    implementation(libs.kotlinx.datetime)
    // logger
    implementation(libs.kermit)
    // cross-platform lib to resolve some application directories for desktop
    implementation(libs.appdirs)
    // An annotation processor for generating type-safe bean mappers
    implementation(libs.mapstruct.core)
    kapt(libs.mapstruct.processor)
    // kamel, media loading and caching (requires Ktor HttpClient)
    implementation(libs.kamel)
    // navigation and view models
    implementation(libs.voyager.navigator.core)
    implementation(libs.voyager.navigator.tab)
    implementation(libs.voyager.transitions)
    // kotest runner
    testImplementation(libs.test.kotest.runner.junit5)
    // kotest assertions
    testImplementation(libs.test.kotest.assertions.core)
    // kotest assertions, kotlinx DateTime assertions
    testImplementation(libs.test.kotest.assertions.kotlinx.time)
    // kotest property-based testing (right now used only for the data generators)
    testImplementation(libs.test.kotest.property)
    // kotest property-based testing, kotlinx DateTime generators
    testImplementation(libs.test.kotest.extensions.property.datetime)
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe)
            packageName = "ghd"
            packageVersion = "${project.version}"
            includeAllModules = true
            macOS {
                iconFile.set(project.file("src/main/resources/icons/custom/ic_launcher.icns"))
            }
            windows {
                menuGroup = ""
                iconFile.set(project.file("src/main/resources/icons/custom/ic_launcher.ico"))
            }
            linux {
                iconFile.set(project.file("src/main/resources/icons/custom/ic_launcher.png"))
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    filter {
        isFailOnNoMatchingTests = false
    }
    testLogging {
        showExceptions = true
        showStandardStreams = true
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.withType<DependencyUpdatesTask> {
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }

    checkForGradleUpdate = true
    gradleReleaseChannel = "current"
    outputFormatter = "plain,html"
    outputDir = "${rootProject.buildDir}/reports/dependencyUpdates"
    reportfileName = "report"
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

