package com.woowla.ghd.domain.entities

import net.swiftzer.semver.SemVer

data class GhdRelease(val tag: String) {
    val version: SemVer by lazy { SemVer.parse(tag.removePrefix("v")) }
}