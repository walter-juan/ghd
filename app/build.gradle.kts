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
import java.io.OutputStream
import java.io.PrintStream

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.compose)
    alias(libs.plugins.apollo3)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.benmanesversions)
}

group = "com.woowla"
version = "1.2.5"
val debug = (extra["debugConfig"] as String).toBoolean()

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
    buildConfigField("String", "APP_VERSION", provider { "\"${project.version}\"" })
    buildConfigField("boolean", "DEBUG", provider { "$debug" })
    buildConfigField("String", "GH_GHD_OWNER", "\"walter-juan\"")
    buildConfigField("String", "GH_GHD_REPO", "\"ghd\"")
    buildConfigField("String", "GH_GHD_LATEST_RELEASE_URL", "\"https://github.com/walter-juan/ghd/releases/latest\"")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.bundles.ktor.client)
    implementation(libs.apollo3)
    implementation(libs.h2)
    implementation(libs.bundles.exposed)
    implementation(libs.kotlinx.datetime)
    implementation(libs.logback.classic)
    implementation(libs.appdirs)
    implementation(libs.mapstruct.core)
    kapt(libs.mapstruct.processor)
    implementation(libs.kamel)
    implementation(libs.bundles.voyager)
    implementation(libs.kaml)
    implementation(libs.semver)
    implementation(libs.flyway.core)

    testImplementation(libs.bundles.test.kotest)
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

    rejectVersionIf {
        isNonStable(candidate.version)
    }

    checkForGradleUpdate = true
    gradleReleaseChannel = "current"
    outputDir = "${rootProject.buildDir}/reports/dependencyUpdates"
    reportfileName = "report"

    outputFormatter {
        val reporters = listOf(
            MarkdownReporter(project = project, revision = revision, gradleReleaseChannel = gradleReleaseChannel),
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
}

class MarkdownReporter(override val project: Project, override val revision: String, override val gradleReleaseChannel: String) : AbstractReporter(project, revision, gradleReleaseChannel) {
    override fun write(printStream: OutputStream, result: Result) {
        writeHeader(printStream)
        if (result.count == 0) {
            printStream.println()
            printStream.println("***No dependencies found.***")
        } else {
            writeUpToDate(printStream, result)
            writeExceedLatestFound(printStream, result)
            writeUpgrades(printStream, result)
            writeUndeclared(printStream, result)
            writeUnresolved(printStream, result)
        }
        writeGradleUpdates(printStream, result)
    }

    override fun getFileExtension(): String {
        return "md"
    }

    private fun writeHeader(printStream: OutputStream) {
        printStream.println("### Project Dependency Updates")
        printStream.println()
        printStream.println("**Project:** ${project.name}")
    }

    private fun writeUpToDate(printStream: OutputStream, result: Result) {
        val deps = result.current.dependencies
        if (deps.isNotEmpty()) {
            printStream.println("<details>")
            printStream.println("<summary>Current dependencies</summary>")
            printStream.println("The following dependencies are using the latest $revision version:")

            printStream.println()
            printStream.println("|Name|Group|URL|Current Version|Reason")
            printStream.println("|----|-----|---|--------------|------|")
            for (dep in deps) {
                printStream.println("" +
                        "|${dep.name.orEmpty()}" +
                        "|${dep.group.orEmpty()}" +
                        "|${getUrlString(dep.projectUrl)}" +
                        "|${getVersionString(dep.group.orEmpty(), dep.name.orEmpty(), dep.version)}" +
                        "|${dep.userReason.orEmpty()}" +
                        "")
            }

            printStream.println("</details>")
        }
    }

    private fun writeExceedLatestFound(printStream: OutputStream, result: Result) {
        val deps = result.exceeded.dependencies
        if (deps.isNotEmpty()) {
            printStream.println("<details>")
            printStream.println("<summary>Exceeded dependencies</summary>")
            printStream.println("The following dependencies exceed the version found at the $revision revision level:")

            printStream.println()
            printStream.println("|Name|Group|URL|Current Version|Latest Version|Reason")
            printStream.println("|----|-----|---|---------------|--------------|------|")
            for (dep in deps) {
                printStream.println("" +
                        "|${dep.name.orEmpty()}" +
                        "|${dep.group.orEmpty()}" +
                        "|${getUrlString(dep.projectUrl)}" +
                        "|${getVersionString(dep.group.orEmpty(), dep.name.orEmpty(), dep.version)}" +
                        "|${getVersionString(dep.group.orEmpty(), dep.name.orEmpty(), dep.version)}" +
                        "|${dep.userReason.orEmpty()}" +
                        "")
            }

            printStream.println("</details>")
        }
    }

    private fun writeUpgrades(printStream: OutputStream, result: Result) {
        val deps = result.outdated.dependencies
        if (deps.isNotEmpty()) {
            printStream.println("<details>")
            printStream.println("<summary>Later dependencies</summary>")
            printStream.println("The following dependencies have later $revision versions:")

            printStream.println()
            printStream.println("|Name|Group|URL|Current Version|Latest Version|Reason")
            printStream.println("|----|-----|---|---------------|--------------|------|")
            for (dep in deps) {
                printStream.println("" +
                        "|${dep.name.orEmpty()}" +
                        "|${dep.group.orEmpty()}" +
                        "|${getUrlString(dep.projectUrl)}" +
                        "|${getVersionString(dep.group.orEmpty(), dep.name.orEmpty(), dep.version)}" +
                        "|${getVersionString(dep.group.orEmpty(), dep.name.orEmpty(), getDisplayableVersion(dep.available))}" +
                        "|${dep.userReason.orEmpty()}" +
                        "")
            }

            printStream.println("</details>")
        }
    }

    private fun writeUndeclared(printStream: OutputStream, result: Result) {
        val deps = result.undeclared.dependencies
        if (deps.isNotEmpty()) {
            printStream.println("<details>")
            printStream.println("<summary>Undeclared dependencies</summary>")
            printStream.println("Failed to compare versions for the following dependencies because they were declared without version:")

            printStream.println()
            printStream.println("|Name|Group|URL|Current Version|Reason")
            printStream.println("|----|-----|---|---------------|------|")
            for (dep in deps) {
                printStream.println("" +
                        "|${dep.name.orEmpty()}" +
                        "|${dep.group.orEmpty()}" +
                        "|${getUrlString(dep.projectUrl)}" +
                        "|${getVersionString(dep.group.orEmpty(), dep.name.orEmpty(), dep.version)}" +
                        "|${dep.userReason.orEmpty()}" +
                        "")
            }

            printStream.println("</details>")
        }
    }

    private fun writeUnresolved(printStream: OutputStream, result: Result) {
        val deps = result.unresolved.dependencies
        if (deps.isNotEmpty()) {
            printStream.println("<details>")
            printStream.println("<summary>Unresolved dependencies</summary>")
            printStream.println("Failed to determine the latest version for the following dependencies:")

            printStream.println()
            printStream.println("|Name|Group|URL|Current Version|Reason")
            printStream.println("|----|-----|---|---------------|------|")
            for (dep in deps) {
                printStream.println("" +
                        "|${dep.name.orEmpty()}" +
                        "|${dep.group.orEmpty()}" +
                        "|${getUrlString(dep.projectUrl)}" +
                        "|${getVersionString(dep.group.orEmpty(), dep.name.orEmpty(), dep.version)}" +
                        "|${dep.userReason.orEmpty()}" +
                        "")
            }

            printStream.println("</details>")
        }
    }

    private fun writeGradleUpdates(printStream: OutputStream, result: Result) {
        if (!result.gradle.enabled) {
            return
        }

        printStream.println("<details>")
        printStream.println("<summary>Gradle updates</summary>")
        printStream.println("Gradle $gradleReleaseChannel updates:")

        // log Gradle update checking failures.
        if (result.gradle.current.isFailure) {
            printStream.println("{ERROR} {release channel: ${CURRENT.id}} " + result.gradle.current.reason)
        }
        if ((gradleReleaseChannel == RELEASE_CANDIDATE.id || gradleReleaseChannel == NIGHTLY.id) && result.gradle.releaseCandidate.isFailure) {
            printStream.println("{ERROR} {release channel: ${RELEASE_CANDIDATE.id}} " + result.gradle.releaseCandidate.reason)
        }
        if (gradleReleaseChannel == NIGHTLY.id && result.gradle.nightly.isFailure) {
            printStream.println("{ERROR} {release channel: ${NIGHTLY.id}} " + result.gradle.nightly.reason )
        }

        // print Gradle updates in breadcrumb format
        printStream.print("Gradle: {" + getGradleVersionUrl(result.gradle.running.version))
        var updatePrinted = false
        if (result.gradle.current.isUpdateAvailable && result.gradle.current > result.gradle.running) {
            updatePrinted = true
            printStream.print(" -> " + getGradleVersionUrl(result.gradle.current.version))
        }
        if ((gradleReleaseChannel == RELEASE_CANDIDATE.id || gradleReleaseChannel == NIGHTLY.id) &&
            result.gradle.releaseCandidate.isUpdateAvailable &&
            result.gradle.releaseCandidate >
            result.gradle.current
        ) {
            updatePrinted = true
            printStream.print(" -> " + getGradleVersionUrl(result.gradle.releaseCandidate.version))
        }
        if (gradleReleaseChannel == NIGHTLY.id &&
            result.gradle.nightly.isUpdateAvailable &&
            result.gradle.nightly >
            result.gradle.current
        ) {
            updatePrinted = true
            printStream.print(" -> " + getGradleVersionUrl(result.gradle.nightly.version))
        }
        if (!updatePrinted) {
            printStream.print(": UP-TO-DATE")
        }
        printStream.println("}")
        printStream.println(getGradleUrl())


        printStream.println("</details>")
    }

    private fun getGradleUrl(): String {
        return "For information about Gradle releases click [here](https://gradle.org/releases/)"
    }

    private fun getGradleVersionUrl(version: String?): String {
        return if (version == null) {
            "https://gradle.org/releases/"
        } else {
            "[$version](https://docs.gradle.org/$version/release-notes.html)"
        }
    }

    private fun getDisplayableVersion(versionAvailable: VersionAvailable): String? {
        if (revision.equals("milestone", ignoreCase = true)) {
            return versionAvailable.milestone
        } else if (revision.equals("release", ignoreCase = true)) {
            return versionAvailable.release
        } else if (revision.equals("integration", ignoreCase = true)) {
            return versionAvailable.integration
        }
        return ""
    }

    private fun getUrlString(url: String?): String {
        return if (url == null) {
            ""
        } else {
            "[$url]($url)"
        }
    }

    private fun getVersionString(group: String, name: String, version: String?): String {
        val mvn = getMvnVersionString(group, name, version)
        return "$version $mvn"
    }

    private fun getMvnVersionString(group: String, name: String, version: String?): String {
        return if (version == null) {
            ""
        } else {
            "[Sonatype](https://search.maven.org/artifact/$group/$name/$version/bundle)"
        }
    }
}
