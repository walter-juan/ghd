package com.woowla.ghd.domain.services

import com.woowla.ghd.BuildConfig
import com.woowla.ghd.data.remote.RemoteDataSource
import net.swiftzer.semver.SemVer

class AppVersionService(
    private val remoteDataSource: RemoteDataSource = RemoteDataSource()
) {

    suspend fun checkForNewVersion(): Result<CheckForNewVersionResponse> {
        return remoteDataSource
            .getLastGhdRelease()
            .mapCatching { apiRelease ->
                val currentVersion = SemVer.parse(BuildConfig.APP_VERSION)
                val latestVersion = SemVer.parse(apiRelease.tag.removePrefix("v"))

                CheckForNewVersionResponse(
                    newVersion = (currentVersion < latestVersion),
                    currentVersion = currentVersion,
                    latestVersion = latestVersion
                )
            }
    }

    data class CheckForNewVersionResponse(val newVersion: Boolean, val currentVersion: SemVer, val latestVersion: SemVer)
}