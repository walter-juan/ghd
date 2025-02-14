import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

group = "com.woowla"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

ksp {
    arg("konvert.enable-converters", "StringToEnumConverter")
}

room {
    schemaDirectory("${projectDir}/src/main/room/schemas")
}

dependencies {
    implementation(project(":domain-api"))
    implementation(project(":core"))

    implementation(libs.kotlinx.datetime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.sqlite.bundled)
    implementation(libs.settings)
    implementation(libs.arrow.optics)
    ksp(libs.arrow.optics.ksp)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.bundles.koin)
    implementation(libs.konvert.api)
    ksp(libs.konvert.ksp)
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}