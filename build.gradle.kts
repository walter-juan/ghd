plugins {
    // this is necessary to avoid the plugins to be loaded multiple times in each subproject's classloader
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.plugin.serialization).apply(false)
    alias(libs.plugins.kapt).apply(false)
    alias(libs.plugins.compose.jetbrains).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.aboutLibraries).apply(false)
    alias(libs.plugins.compose.hotreload) apply false
}