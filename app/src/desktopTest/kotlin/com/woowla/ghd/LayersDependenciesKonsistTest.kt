package com.woowla.ghd

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import io.kotest.core.spec.style.FreeSpec

class LayersDependenciesKonsistTest : FreeSpec({
    "layers have correct dependencies" {
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
                val app = Layer("App", "com.woowla.ghd.app..")
                val core = Layer("Core", "com.woowla.ghd.core..")
                val data = Layer("Data", "com.woowla.ghd.data..")
                val domain = Layer("Domain", "com.woowla.ghd.domain..")
                val presentation = Layer("Presentation", "com.woowla.ghd.presentation..")

                core.dependsOnNothing()

                domain.dependsOn(core)
                domain.doesNotDependOn(app, data, presentation)

                presentation.dependsOn(core, domain)
                presentation.doesNotDependOn(app, data)

                data.dependsOn(core, domain)
                data.doesNotDependOn(app, presentation)
            }
    }
})