package com.woowla.ghd.domain.services

import net.swiftzer.semver.SemVer

interface AppVersionService {
    suspend fun checkForNewVersion(): Result<CheckForNewVersionResponse>
    data class CheckForNewVersionResponse(val newVersion: Boolean, val currentVersion: SemVer, val latestVersion: SemVer)
}