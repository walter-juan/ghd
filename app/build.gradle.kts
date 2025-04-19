import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.reporter.HtmlReporter
import com.github.benmanes.gradle.versions.reporter.PlainTextReporter
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.PrintStream

plugins {
    alias(libs.plugins.kotlin.jvm)
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
    alias(libs.plugins.detekt)
}

group = "com.woowla"
version = "2.0.5"
// Required for JPackage, as it doesn't accept additional suffixes after the version.
val versionSimplified = version.toString().substringBefore("-")
val debug = (extra["debugConfig"] as String).toBoolean()
val debugAppFolder = "ghd-debug"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

detekt {
    config.from(files("$rootDir/config/detekt/detekt-config.yml"))
    baseline = file("$rootDir/config/detekt/detekt-baseline.xml")
    buildUponDefaultConfig = true
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
    schemaDirectory("${projectDir}/src/main/room/schemas")
}

apollo {
    service("github") {
        packageName.set("com.woowla.ghd.data.remote")
        generateOptionalOperationVariables.set(false)
    }
}

aboutLibraries {
    android.registerAndroidTasks = false
    export.prettyPrint = true
    export.excludeFields.addAll("description", "funding")
}

dependencies {
    implementation(compose.desktop.currentOs)
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.material3)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.logback.classic)
    implementation(libs.semver)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.bundles.coil)
    implementation(libs.icons.tabler)
    implementation(libs.bundles.flowredux)
    implementation(libs.composenativetray)
    implementation(libs.materialkolor)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.bundles.koin)
    implementation(libs.arrow.optics)
    ksp(libs.arrow.optics.ksp)
    implementation(libs.kotlinx.datetime)
    implementation(libs.aboutlibraries)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.bundles.ktor.client)
    implementation(libs.appdirs)
    implementation(libs.kaml)
    implementation(libs.settings)
    implementation(libs.apollo3)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.sqlite.bundled)
    implementation(libs.konvert.api)
    ksp(libs.konvert.ksp)

    testImplementation(libs.test.mockk)
    testImplementation(libs.bundles.test.kotest)
    testImplementation(libs.test.konsist)

    detektPlugins(libs.detekt.formatting)
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

// disabling detekt from the check task
tasks.named("check").configure {
    this.setDependsOn(this.dependsOn.filterNot {
        it is TaskProvider<*> && it.name == "detekt"
    })
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
