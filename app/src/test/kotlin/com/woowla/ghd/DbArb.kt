package com.woowla.ghd

import com.woowla.ghd.data.local.DbAppSettings
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import kotlinx.datetime.Clock

object DbArb {
    val dbAppSettingsArb = arbitrary {
        DbAppSettings(
            id = Arb.string().bind(),
            githubPatToken = Arb.string().bind(),
            checkTimeout = Arb.long().bind(),
            synchronizedAt = Clock.System.now().toString(),
            appDarkTheme = Arb.boolean().bind(),
            pullRequestCleanUpTimeout = Arb.long().bind(),
        )
    }
}