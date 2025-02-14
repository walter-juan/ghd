import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.jetbrains)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.benmanesversions)
}

group = "com.woowla"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(project(":domain-api"))
    implementation(project(":core"))

    implementation(compose.desktop.currentOs)
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.material3)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.bundles.ktor.client)
    implementation(libs.kotlinx.datetime)
    implementation(libs.logback.classic)
    implementation(libs.appdirs)
    implementation(libs.bundles.coil)
    implementation(libs.kaml)
    implementation(libs.semver)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.sqlite.bundled)
    implementation(libs.settings)
    implementation(libs.icons.tabler)
    implementation(libs.composenativetray)
    implementation(libs.bundles.flowredux)
    implementation(libs.materialkolor)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    testImplementation(libs.mockk)
    testImplementation(libs.bundles.test.kotest)
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