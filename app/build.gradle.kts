import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.reporter.result.Result
import com.github.benmanes.gradle.versions.reporter.result.VersionAvailable
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel.CURRENT
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel.NIGHTLY
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel.RELEASE_CANDIDATE
import com.github.benmanes.gradle.versions.reporter.HtmlReporter
import com.github.benmanes.gradle.versions.reporter.PlainTextReporter
import com.github.benmanes.gradle.versions.reporter.AbstractReporter
import com.github.benmanes.gradle.versions.reporter.print
import com.github.benmanes.gradle.versions.reporter.println
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.OutputStream
import java.io.PrintStream

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.jetbrains)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.apollo3)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.androidx.room)
}

group = "com.woowla"
version = "1.5.3"
val debug = (extra["debugConfig"] as String).toBoolean()
val debugAppFolder = "ghd-debug"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

apollo {
    service("github") {
        packageName.set("com.woowla.ghd.data.remote")
        generateOptionalOperationVariables.set(false)
    }
}

buildConfig {
    packageName("com.woowla.ghd")
    buildConfigField("APP_VERSION", project.version.toString())
    buildConfigField("DEBUG", debug)
    buildConfigField("DEBUG_APP_FOLDER", debugAppFolder)
    buildConfigField("GH_GHD_OWNER", "walter-juan")
    buildConfigField("GH_GHD_REPO", "ghd")
    buildConfigField("GH_GHD_LATEST_RELEASE_URL", "https://github.com/walter-juan/ghd/releases/latest")
}

dependencies {
    implementation(compose.desktop.currentOs)
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.bundles.ktor.client)
    implementation(libs.apollo3)
    implementation(libs.kotlinx.datetime)
    implementation(libs.logback.classic)
    implementation(libs.appdirs)
    implementation(libs.kamel)
    implementation(libs.kaml)
    implementation(libs.semver)
    implementation(libs.tinder.statemachine)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.sqlite.bundled)
    implementation(libs.settings)
    implementation(libs.icons.tabler)

    testImplementation(libs.bundles.test.kotest)
}

room {
    schemaDirectory("${projectDir}/src/main/room/schemas")
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
                iconFile.set(project.file("src/main/resources/icons/ic_launcher.icns"))
            }
            windows {
                menuGroup = ""
                iconFile.set(project.file("src/main/resources/icons/ic_launcher.ico"))
            }
            linux {
                iconFile.set(project.file("src/main/resources/icons/ic_launcher.png"))
            }
        }
    }
}

tasks.register("ghdCleanDebugAppFolder") {
    description = "Clean the debug app folder"
    group = "GHD"
    doLast {
        val file = rootProject.file(debugAppFolder)
        if (file.exists()) {
            file.deleteRecursively()
        }
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
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