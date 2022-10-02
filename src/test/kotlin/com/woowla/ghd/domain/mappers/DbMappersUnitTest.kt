package com.woowla.ghd.domain.mappers

import com.woowla.ghd.DbArb
import com.woowla.ghd.domain.entities.AppSettings
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next

class DbMappersUnitTest : StringSpec({
    "dbAppSettingsToAppSettings with null checkTimeout should return the default" {
        val dbAppSettings = DbArb.dbAppSettingsArb.next().copy(checkTimeout = null)
        val appSettings = DbMappers.INSTANCE.dbAppSettingsToAppSettings(dbAppSettings)
        appSettings.checkTimeout
            .shouldNotBeNull()
            .shouldBe(AppSettings.defaultCheckTimeout)
    }
})