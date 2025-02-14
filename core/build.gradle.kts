import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.jetbrains)
    alias(libs.plugins.compose.compiler)
}

group = "com.woowla"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.material3)
    implementation(libs.kotlinx.coroutines.swing)

    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.logback.classic)
    implementation(libs.appdirs)
    implementation(libs.bundles.flowredux)
    implementation(libs.materialkolor)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.bundles.koin)

    testImplementation(libs.bundles.test.kotest)
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
