import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.compose")
    id("com.squareup.sqldelight")
    id("com.apollographql.apollo3")
    id("com.github.gmazzo.buildconfig")
    id("com.github.ben-manes.versions")
}

group = "com.woowla"
version = "1.0.4"
val debug = (extra["debugConfig"] as String).toBoolean()

val ktorVersion = extra["ktor.version"] as String
val realmVersion = extra["realm.version"] as String
val kotestVersion = extra["kotest.version"] as String
val sqldelightVersion = extra["sqldelight.version"] as String
val apollo3Version = extra["apollo3.version"] as String

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
    buildConfigField("String", "APP_NAME", "\"${project.name}\"")
    buildConfigField("String", "APP_VERSION", provider { "\"${project.version}\"" })
    buildConfigField("boolean", "DEBUG", provider { "$debug" })
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    // kotlin coroutines, needed because we are using the main dispatcher
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
    // ktor http client (needed for kamel)
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    // GraphQL client
    implementation("com.apollographql.apollo3:apollo-runtime:$apollo3Version")
    // sqldelight database for JVM
    implementation("com.squareup.sqldelight:sqlite-driver:$sqldelightVersion")
    // date time kotlin lib
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    // logger
    implementation("co.touchlab:kermit:1.1.3")
    // cross-platform lib to resolve some application directories for desktop
    implementation("net.harawata:appdirs:1.2.1")
    // An annotation processor for generating type-safe bean mappers
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.3.Final")
    // kamel, media loading and caching (requires Ktor HttpClient)
    implementation("com.alialbaali.kamel:kamel-image:0.4.1")
    // navigation and view models
    implementation("cafe.adriel.voyager:voyager-navigator:1.0.0-rc02")
    implementation("cafe.adriel.voyager:voyager-tab-navigator:1.0.0-rc02")
    implementation("cafe.adriel.voyager:voyager-transitions:1.0.0-rc02")
    // kotest runner
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    // kotest assertions
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    // kotest assertions, kotlinx DateTime assertions
    testImplementation("io.kotest:kotest-assertions-kotlinx-time:4.4.3")
    // kotest property-based testing (right now used only for the data generators)
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    // kotest property-based testing, kotlinx DateTime generators
    testImplementation("io.kotest.extensions:kotest-property-datetime:1.1.0")
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
