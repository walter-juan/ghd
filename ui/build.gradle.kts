import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.jetbrains)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
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
    implementation(libs.bundles.coil)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.icons.tabler)
    implementation(libs.bundles.flowredux)
    implementation(libs.arrow.optics)
    ksp(libs.arrow.optics.ksp)
    implementation(libs.kotlinx.datetime)
    implementation(libs.materialkolor)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.bundles.koin)
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}
