import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
}

group = "com.woowla"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.arrow.optics)
    implementation(libs.semver)
    ksp(libs.arrow.optics.ksp)
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}