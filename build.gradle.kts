import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.report.ReportMergeTask

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times in each subproject's classloader
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlin.plugin.serialization).apply(false)
    alias(libs.plugins.kapt).apply(false)
    alias(libs.plugins.compose.jetbrains).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.detekt)
    alias(libs.plugins.aboutLibraries).apply(false)
}

val detektReportMergeXml by tasks.registering(ReportMergeTask::class) {
    output = layout.buildDirectory.file("reports/detekt/merge.xml")
}

allprojects {
    val libs = rootProject.libs // using libs directly doesn't work, see https://github.com/gradle/gradle/issues/16634#issuecomment-809345790
    apply(plugin = libs.plugins.detekt.get().pluginId)
    detekt {
        val projectName = project.name.trim(':').replace(":", "-")
        config.from(files("$rootDir/config/detekt/detekt-config.yml"))
        baseline = file("$rootDir/config/detekt/detekt-baseline-$projectName.xml")
        buildUponDefaultConfig = true
    }
    dependencies {
        detektPlugins(libs.detekt.formatting)
    }
    tasks.withType<Detekt>().configureEach {
        finalizedBy(detektReportMergeXml)
    }
    detektReportMergeXml {
        input.from(tasks.withType<Detekt>().map { it.xmlReportFile })
    }
}

val detektProjectBaseline by tasks.registering(DetektCreateBaselineTask::class) {
    description = "Overrides current baseline."
    buildUponDefaultConfig.set(true)
    ignoreFailures.set(true)
    parallel.set(true)
    setSource(files(rootDir))
    config.setFrom(files("$rootDir/config/detekt/detekt-config.yml"))
    baseline.set(file("$rootDir/config/detekt/detekt-baseline.xml"))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
}