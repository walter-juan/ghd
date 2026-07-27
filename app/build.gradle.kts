import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.jetbrains)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.benmanesversions)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.apollo3)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.compose.hotreload)
}

group = "com.woowla"
version = "2.0.6-beta05"
// Required for JPackage, as it doesn't accept additional suffixes after the version.
val versionSimplified = version.toString().substringBefore("-")
val debug = (extra["debugConfig"] as String).toBoolean()
val debugAppFolder = "ghd-debug"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

buildConfig {
    packageName("com.woowla.ghd")
    buildConfigField("APP_VERSION", project.version.toString())
    buildConfigField("APP_VERSION_SIMPLIFIED", versionSimplified)
    buildConfigField("APP_VERSION_PRE_RELEASE", version.toString().substringAfter(delimiter = "-", missingDelimiterValue = ""))
    buildConfigField("DEBUG", debug)
    buildConfigField("DEBUG_APP_FOLDER", debugAppFolder)
    buildConfigField("GH_GHD_OWNER", "walter-juan")
    buildConfigField("GH_GHD_REPO", "ghd")
    buildConfigField("GH_GHD_LATEST_RELEASE_URL", "https://github.com/walter-juan/ghd/releases/latest")
}

ksp {
    arg("konvert.enable-converters", "StringToEnumConverter")
}

room {
    schemaDirectory("${projectDir}/src/commonMain/room/schemas")
}

apollo {
    service("github") {
        packageName.set("com.woowla.ghd.data.remote")
        generateOptionalOperationVariables.set(false)
    }
}

aboutLibraries {
    export {
        prettyPrint = true
        excludeFields.addAll("description", "funding")
        outputFile = file("src/desktopMain/resources/aboutlibraries.json")
    }
    library {
        duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
        duplicationRule = com.mikepenz.aboutlibraries.plugin.DuplicateRule.SIMPLE
    }
}


kotlin {
    jvm("desktop")

    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime") // For kotlinx-datetime was moved to kotlin.time
    }

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.semver)
            implementation(libs.bundles.coil)
            implementation(libs.icons.tabler)
            implementation(libs.bundles.flowredux)
            implementation(libs.materialkolor)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.bundles.koin)
            implementation(libs.arrow.optics)
            implementation(libs.kotlinx.datetime)
            implementation(libs.aboutlibraries)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.bundles.ktor.client)
            implementation(libs.settings)
            implementation(libs.apollo3)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }
        commonTest.dependencies {
            implementation(libs.test.mockk)
            implementation(libs.bundles.test.kotest)
            implementation(libs.test.konsist)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(compose.material3)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.logback.classic)
            implementation(libs.appdirs)
            implementation(libs.kaml)
            implementation(libs.konvert.api)
        }
    }
}

dependencies {
    add("kspDesktop", libs.arrow.optics.ksp)
    add("kspDesktop", libs.androidx.room.compiler)
    add("kspDesktop", libs.konvert.ksp)
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe)
            packageName = "ghd"
            packageVersion = versionSimplified
            includeAllModules = true
            val iconName = if (debug) { "ic_launcher_debug" } else { "ic_launcher" }
            macOS {
                iconFile.set(project.file("src/desktopMain/resources/icons/$iconName.icns"))
            }
            windows {
                menuGroup = ""
                iconFile.set(project.file("src/desktopMain/resources/icons/$iconName.ico"))
            }
            linux {
                iconFile.set(project.file("src/desktopMain/resources/icons/$iconName.png"))
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

tasks.withType<DependencyUpdatesTask>().configureEach {
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }

    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }

    checkForGradleUpdate = true
    gradleReleaseChannel = "current"

}

tasks.named("dependencyUpdates") {
    doLast("openDependencyUpdatesReport") {
        val openBrowser = providers.gradleProperty("openBrowser").map(String::toBoolean).orElse(false).get()
        val outputDir = System.getProperty("outputDir") ?: layout.buildDirectory.dir("dependencyUpdates").get().asFile.path
        val reportFileName = System.getProperty("reportfileName") ?: "report"
        val htmlReportFile = file(outputDir).resolve("$reportFileName.html")

        logger.lifecycle("Open browser: $openBrowser")
        logger.lifecycle("Dependency updates HTML report: $htmlReportFile")

        if (openBrowser) {
            openDependencyUpdatesReport(file = htmlReportFile)
        }
    }
}

fun openDependencyUpdatesReport(file: File) {
    logger.lifecycle("Opening dependency updates report: $file")
    require(file.isFile) { "Dependency updates HTML report does not exist: ${file.absoluteFile}" }

    val os = org.gradle.internal.os.OperatingSystem.current()
    val command = when {
        os.isMacOsX -> "open"
        os.isLinux -> "xdg-open"
        else -> error("Unsupported operating system for opening a dependency updates report: ${os.name}")
    }

    val execOperations = project.serviceOf<ExecOperations>()
    execOperations.exec { commandLine(command, file) }
}
