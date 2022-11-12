package com.woowla.ghd.domain.usecases

import com.woowla.ghd.domain.entities.Release
import com.woowla.ghd.notifications.NotificationsSender
import com.woowla.ghd.utils.UseCase

class SendReleasesNotificationsUseCase(
    private val notificationsSender: NotificationsSender = NotificationsSender(),
): UseCase<SendReleasesNotificationsUseCase.Params, Unit>() {
    data class Params(val oldReleases: List<Release>, val newReleases: List<Release>)

    override suspend fun perform(params: Params): Result<Unit> {
        val oldReleasesIds = params.oldReleases.map { it.id }

        // notification for a new release
        params.newReleases
            .filter {
                it.repoToCheck.releaseNotificationsEnabled
            }
            .filterNot {
                oldReleasesIds.contains(it.id)
            }
            .forEach { newRelease ->
                notificationsSender.newRelease(newRelease)
            }

        // notification for an update
        params.newReleases
            .filter {
                it.repoToCheck.releaseNotificationsEnabled
            }
            .filter { newRelease ->
                val oldRelease = params.oldReleases.firstOrNull { it.id == newRelease.id }

                if (oldRelease != null) {
                    oldRelease.publishedAt != newRelease.publishedAt
                } else {
                    false
                }
            }
            .forEach { newRelease ->
                notificationsSender.updateRelease(newRelease)
            }

        return Result.success(Unit)
    }
}