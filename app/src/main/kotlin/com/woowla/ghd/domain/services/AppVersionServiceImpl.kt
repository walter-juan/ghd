package com.woowla.ghd.domain.services

import com.woowla.ghd.domain.data.RemoteDataSource
import net.swiftzer.semver.SemVer

class AppVersionServiceImpl(
    private val currentVersion: SemVer,
    private val remoteDataSource: RemoteDataSource,
) : AppVersionService {
    override suspend fun checkForNewVersion(): Result<AppVersionService.CheckForNewVersionResponse> {
        return remoteDataSource
            .getLastGhdRelease()
            .mapCatching { apiResponse ->
                val ghdRelease = apiResponse.data

                AppVersionService.CheckForNewVersionResponse(
                    newVersion = (currentVersion < ghdRelease.version),
                    currentVersion = currentVersion,
                    latestVersion = ghdRelease.version
                )
            }
    }

}