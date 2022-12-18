package com.woowla.ghd.domain.usecases

import com.woowla.ghd.BuildConfig
import com.woowla.ghd.data.remote.RemoteDataSource
import com.woowla.ghd.utils.UseCaseWithoutParams
import net.swiftzer.semver.SemVer

class GetNewAppVersionUseCase(
    private val remoteDataSource: RemoteDataSource = RemoteDataSource()
) : UseCaseWithoutParams<GetNewAppVersionUseCase.Response>() {

    override suspend fun perform(): Result<Response> {
        return remoteDataSource
            .getLastGhdRelease()
            .map { apiRelease ->
                val currentVersion = SemVer.parse(BuildConfig.APP_VERSION)
                val latestVersion = SemVer.parse(apiRelease.tag.removePrefix("v"))

                Response(
                    newVersion = (currentVersion < latestVersion),
                    currentVersion = currentVersion,
                    latestVersion = latestVersion
                )
            }
    }

    data class Response(val newVersion: Boolean, val currentVersion: SemVer, val latestVersion: SemVer)
}