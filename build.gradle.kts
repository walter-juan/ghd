plugins {
    // this is necessary to avoid the plugins to be loaded multiple times in each subproject's classloader
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlin.plugin.serialization).apply(false)
    alias(libs.plugins.kapt).apply(false)
    alias(libs.plugins.compose.jetbrains).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.aboutLibraries).apply(false)
}

//val detektProjectBaseline by tasks.registering(DetektCreateBaselineTask::class) {
//    description = "Overrides current baseline."
//    buildUponDefaultConfig.set(true)
//    ignoreFailures.set(true)
//    parallel.set(true)
//    setSource(files(rootDir))
//    config.setFrom(files("$rootDir/config/detekt/detekt-config.yml"))
//    baseline.set(file("$rootDir/config/detekt/detekt-baseline.xml"))
//    include("**/*.kt")
//    include("**/*.kts")
//    exclude("**/resources/**")
//    exclude("**/build/**")
//}