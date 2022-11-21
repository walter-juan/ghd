package com.woowla.ghd.domain.usecases

import com.woowla.ghd.domain.entities.PullRequest
import com.woowla.ghd.notifications.NotificationsSender
import com.woowla.ghd.utils.UseCase

class SendPullRequestsNotificationsUseCase(
    private val notificationsSender: NotificationsSender = NotificationsSender(),
): UseCase<SendPullRequestsNotificationsUseCase.Params, Unit>() {
    data class Params(val oldPullRequests: List<PullRequest>, val newPullRequests: List<PullRequest>)

    override suspend fun perform(params: Params): Result<Unit> {
        val oldPullRequestIds = params.oldPullRequests.map { it.id }

        // notification for a new pull requests
        params.newPullRequests
            .filter {
                it.repoToCheck.pullNotificationsEnabled
            }
            .filterNot {
                oldPullRequestIds.contains(it.id)
            }
            .forEach {
                notificationsSender.newPullRequest(it)
            }

        // notification for an update
        params.newPullRequests
            .filter {
                it.repoToCheck.pullNotificationsEnabled
            }
            .filter {
                !it.appSeen
            }
            .filter { newPull ->
                val oldRelease = params.oldPullRequests.firstOrNull { it.id == newPull.id }

                if (oldRelease != null) {
                    oldRelease.updatedAt != newPull.updatedAt
                } else {
                    false
                }
            }
            .forEach { newPull ->
                notificationsSender.updatePullRequest(newPull)
            }

        return Result.success(Unit)
    }
}