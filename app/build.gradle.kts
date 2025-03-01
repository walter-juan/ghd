import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.reporter.HtmlReporter
import com.github.benmanes.gradle.versions.reporter.PlainTextReporter
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.PrintStream

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.jetbrains)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.benmanesversions)
    alias(libs.plugins.aboutLibraries)
}

group = "com.woowla"
version = "2.0.4-beta03"
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

aboutLibraries {
    registerAndroidTasks = false
    prettyPrint = true
    excludeFields = arrayOf("description", "funding")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain-api"))
    implementation(project(":domain-impl"))
    implementation(project(":data"))
    implementation(project(":ui"))

    implementation(compose.desktop.currentOs)
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.material3)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.logback.classic)
    implementation(libs.semver)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.icons.tabler)
    implementation(libs.composenativetray)
    implementation(libs.materialkolor)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.bundles.koin)
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
                iconFile.set(project.file("src/main/resources/icons/$iconName.icns"))
            }
            windows {
                menuGroup = ""
                iconFile.set(project.file("src/main/resources/icons/$iconName.ico"))
            }
            linux {
                iconFile.set(project.file("src/main/resources/icons/$iconName.png"))
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

tasks.withType<DependencyUpdatesTask> {
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

    outputFormatter {
        val reporters = listOf(
            HtmlReporter(project = project, revision = revision, gradleReleaseChannel = gradleReleaseChannel),
            PlainTextReporter(project = project, revision = revision, gradleReleaseChannel = gradleReleaseChannel),
        )

        reporters.forEach { reporter ->
            val fileName = File(outputDir, reportfileName + "." + reporter.getFileExtension())
            project.file(outputDir).mkdirs()
            val outputFile = project.file(fileName)
            val stream = PrintStream(outputFile)
            reporter.write(stream, this@outputFormatter)
            stream.close()
        }
    }
    val openBrowser: Boolean by project.extra { (findProperty("openBrowser") as? String)?.toBoolean() ?: false }
    val htmlReportFile = File(project.projectDir, outputDir).resolve("$reportfileName.html")

    doLast {
        if (openBrowser) {
            openBrowser(file = htmlReportFile)
        }
    }
}

fun openBrowser(file: File) {
    require(file.exists()) { "File [${file.absoluteFile}] doesn't exists" }

    val os = org.gradle.internal.os.OperatingSystem.current()
    if (os.isMacOsX || os.isLinux) {
        logger.info("Open $file")
        exec { commandLine("open", file) }
    } else {
        logger.error("Non-supported operating system to open a file ${os.name}")
    }
}
