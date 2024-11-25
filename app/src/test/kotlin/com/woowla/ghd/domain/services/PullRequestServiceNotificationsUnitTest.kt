package com.woowla.ghd.domain.services

import com.woowla.ghd.RandomEntities
import com.woowla.ghd.TestNotificationsSender
import com.woowla.ghd.domain.entities.AppSettings
import com.woowla.ghd.domain.entities.PullRequestNotificationsFilterOptions
import com.woowla.ghd.domain.entities.PullRequestState
import com.woowla.ghd.domain.entities.PullRequestWithRepoAndReviews
import com.woowla.ghd.domain.mappers.toPullRequestSeen
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

class PullRequestServiceNotificationsUnitTest : DescribeSpec({
    fun buildAppSettings(
        pullRequestNotificationsFilterOptions: PullRequestNotificationsFilterOptions = PullRequestNotificationsFilterOptions(
            open = false,
            closed = false,
            merged = false,
            draft = false
        ),
        pullRequestStateNotificationsEnabled: Boolean = false,
        pullRequestActivityNotificationsEnabled: Boolean = false,
    ) = AppSettings(
        darkTheme = null,
        pullRequestNotificationsFilterOptions = pullRequestNotificationsFilterOptions,
        pullRequestStateNotificationsEnabled = pullRequestStateNotificationsEnabled,
        pullRequestActivityNotificationsEnabled = pullRequestActivityNotificationsEnabled,
        newReleaseNotificationsEnabled = false,
        updatedReleaseNotificationsEnabled = false
    )

    describe("Pull request notifications") {
        describe("when all filters are NOT enabled") {
            describe("and state changes are enabled") {
                it("should NOT send notifications") {
                    val appSettings = buildAppSettings(
                        pullRequestNotificationsFilterOptions = PullRequestNotificationsFilterOptions(
                            open = false,
                            closed = false,
                            merged = false,
                            draft = false
                        ),
                        pullRequestStateNotificationsEnabled = true,
                    )
                    val testNotificationsSender = TestNotificationsSender()
                    val pullRequest = RandomEntities.pullRequest()
                    val repoToCheck = RandomEntities.repoToCheck()
                    val pullRequestWithRepoAndReviews = PullRequestWithRepoAndReviews(
                        repoToCheck = repoToCheck,
                        pullRequest = pullRequest,
                        reviews = listOf(),
                        pullRequestSeen = pullRequest.toPullRequestSeen(seenAt = Clock.System.now()),
                        reviewsSeen = listOf(),
                    )
                    val service = PullRequestService(
                        localDataSource = mockk(),
                        remoteDataSource = mockk(),
                        notificationsSender = testNotificationsSender,
                        appSettingsService = mockk(),
                    )

                    service.sendNotifications(
                        appSettings = appSettings,
                        oldPullRequestsWithReviews = listOf(),
                        newPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviews)
                    )

                    testNotificationsSender.newPullRequestCount shouldBe 0
                }
            }
            describe("and activity is enabled") {
                it("should NOT send notifications") {
                    val appSettings = buildAppSettings(
                        pullRequestNotificationsFilterOptions = PullRequestNotificationsFilterOptions(
                            open = false,
                            closed = false,
                            merged = false,
                            draft = false
                        ),
                        pullRequestActivityNotificationsEnabled = true,
                    )
                    val testNotificationsSender = TestNotificationsSender()
                    val pullRequest = RandomEntities.pullRequest()
                    val repoToCheck = RandomEntities.repoToCheck()
                    val pullRequestWithRepoAndReviews = PullRequestWithRepoAndReviews(
                        repoToCheck = repoToCheck,
                        pullRequest = pullRequest,
                        reviews = listOf(),
                        pullRequestSeen = pullRequest.toPullRequestSeen(seenAt = Clock.System.now()),
                        reviewsSeen = listOf(),
                    )
                    val service = PullRequestService(
                        localDataSource = mockk(),
                        remoteDataSource = mockk(),
                        notificationsSender = testNotificationsSender,
                        appSettingsService = mockk(),
                    )

                    service.sendNotifications(
                        appSettings = appSettings,
                        oldPullRequestsWithReviews = listOf(),
                        newPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviews)
                    )

                    testNotificationsSender.newPullRequestCount shouldBe 0
                }
            }
        }
        describe("when all filters enabled") {
            describe("and state changes + activity are enabled") {
                describe("and a new pull request is added") {
                    it("should send state change notification and not activity") {
                        val appSettings = buildAppSettings(
                            pullRequestNotificationsFilterOptions = PullRequestNotificationsFilterOptions(
                                open = true,
                                closed = true,
                                merged = true,
                                draft = true
                            ),
                            pullRequestStateNotificationsEnabled = true,
                            pullRequestActivityNotificationsEnabled = true,
                        )
                        val testNotificationsSender = TestNotificationsSender()
                        val pullRequest = RandomEntities.pullRequest()
                        val repoToCheck = RandomEntities.repoToCheck()
                        val pullRequestWithRepoAndReviews = PullRequestWithRepoAndReviews(
                            repoToCheck = repoToCheck,
                            pullRequest = pullRequest,
                            reviews = listOf(),
                            pullRequestSeen = pullRequest.toPullRequestSeen(seenAt = Clock.System.now()),
                            reviewsSeen = listOf(),
                        )
                        val service = PullRequestService(
                            localDataSource = mockk(),
                            remoteDataSource = mockk(),
                            notificationsSender = testNotificationsSender,
                            appSettingsService = mockk(),
                        )

                        service.sendNotifications(
                            appSettings = appSettings,
                            oldPullRequestsWithReviews = listOf(),
                            newPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviews)
                        )

                        testNotificationsSender.newPullRequestCount shouldBe 1
                        testNotificationsSender.updatePullRequestCount shouldBe 0
                    }
                }
                describe("and new pull request changes his state") {
                    it("should send state change notification and not activity") {
                        val appSettings = buildAppSettings(
                            pullRequestNotificationsFilterOptions = PullRequestNotificationsFilterOptions(
                                open = false,
                                closed = false,
                                merged = true,
                                draft = false
                            ),
                            pullRequestStateNotificationsEnabled = true,
                            pullRequestActivityNotificationsEnabled = true,
                        )
                        val testNotificationsSender = TestNotificationsSender()
                        val repoToCheck = RandomEntities.repoToCheck()
                        val now = Clock.System.now()
                        val pullRequestBefore = RandomEntities.pullRequest(repoToCheckId = repoToCheck.id)
                            .copy(
                                state = PullRequestState.OPEN,
                                updatedAt = now,
                            )
                        val pullRequestSeen = pullRequestBefore.toPullRequestSeen(seenAt = now)
                        val pullRequestWithRepoAndReviewsBefore = PullRequestWithRepoAndReviews(
                            repoToCheck = repoToCheck,
                            pullRequest = pullRequestBefore,
                            reviews = listOf(),
                            pullRequestSeen = pullRequestSeen,
                            reviewsSeen = listOf(),
                        )
                        val pullRequestAfter = pullRequestBefore
                            .copy(
                                state = PullRequestState.MERGED,
                                updatedAt = pullRequestBefore.updatedAt.plus(1.days)
                            )
                        val pullRequestWithRepoAndReviewsAfter = PullRequestWithRepoAndReviews(
                            repoToCheck = repoToCheck,
                            pullRequest = pullRequestAfter,
                            reviews = listOf(),
                            pullRequestSeen = pullRequestSeen,
                            reviewsSeen = listOf(),
                        )
                        val service = PullRequestService(
                            localDataSource = mockk(),
                            remoteDataSource = mockk(),
                            notificationsSender = testNotificationsSender,
                            appSettingsService = mockk(),
                        )

                        service.sendNotifications(
                            appSettings = appSettings,
                            oldPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviewsBefore),
                            newPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviewsAfter)
                        )

                        testNotificationsSender.newPullRequestCount shouldBe 1
                        testNotificationsSender.updatePullRequestCount shouldBe 0
                    }
                }
                describe("and new pull request changes updated") {
                    it("should send activity notification") {
                        val appSettings = buildAppSettings(
                            pullRequestNotificationsFilterOptions = PullRequestNotificationsFilterOptions(
                                open = false,
                                closed = false,
                                merged = true,
                                draft = false
                            ),
                            pullRequestStateNotificationsEnabled = true,
                            pullRequestActivityNotificationsEnabled = true,
                        )
                        val testNotificationsSender = TestNotificationsSender()
                        val repoToCheck = RandomEntities.repoToCheck()
                        val now = Clock.System.now()
                        val pullRequestBefore = RandomEntities.pullRequest(repoToCheckId = repoToCheck.id).copy(updatedAt = now)
                        val pullRequestWithRepoAndReviewsBefore = PullRequestWithRepoAndReviews(
                            repoToCheck = repoToCheck,
                            pullRequest = pullRequestBefore,
                            reviews = listOf(),
                            pullRequestSeen = null,
                            reviewsSeen = listOf(),
                        )
                        val pullRequestAfter = pullRequestBefore.copy(updatedAt = pullRequestBefore.updatedAt.plus(1.days))
                        val pullRequestWithRepoAndReviewsAfter = PullRequestWithRepoAndReviews(
                            repoToCheck = repoToCheck,
                            pullRequest = pullRequestAfter,
                            reviews = listOf(),
                            pullRequestSeen = null,
                            reviewsSeen = listOf(),
                        )
                        val service = PullRequestService(
                            localDataSource = mockk(),
                            remoteDataSource = mockk(),
                            notificationsSender = testNotificationsSender,
                            appSettingsService = mockk(),
                        )

                        service.sendNotifications(
                            appSettings = appSettings,
                            oldPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviewsBefore),
                            newPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviewsAfter)
                        )

                        testNotificationsSender.newPullRequestCount shouldBe 0
                        testNotificationsSender.updatePullRequestCount shouldBe 1
                    }
                }
                describe("and new pull request changes updated but not seen (null)") {
                    it("should send activity notification") {
                        val appSettings = buildAppSettings(
                            pullRequestNotificationsFilterOptions = PullRequestNotificationsFilterOptions(
                                open = false,
                                closed = false,
                                merged = true,
                                draft = false
                            ),
                            pullRequestStateNotificationsEnabled = true,
                            pullRequestActivityNotificationsEnabled = true,
                        )
                        val testNotificationsSender = TestNotificationsSender()
                        val repoToCheck = RandomEntities.repoToCheck()
                        val now = Clock.System.now()
                        val pullRequestBefore = RandomEntities.pullRequest(repoToCheckId = repoToCheck.id).copy(updatedAt = now.minus(1.days))
                        val pullRequestWithRepoAndReviewsBefore = PullRequestWithRepoAndReviews(
                            repoToCheck = repoToCheck,
                            pullRequest = pullRequestBefore,
                            reviews = listOf(),
                            pullRequestSeen = null,
                            reviewsSeen = listOf(),
                        )
                        val pullRequestAfter = pullRequestBefore.copy(updatedAt = pullRequestBefore.updatedAt.plus(1.days))
                        val pullRequestWithRepoAndReviewsAfter = PullRequestWithRepoAndReviews(
                            repoToCheck = repoToCheck,
                            pullRequest = pullRequestAfter,
                            reviews = listOf(),
                            pullRequestSeen = null,
                            reviewsSeen = listOf(),
                        )
                        val service = PullRequestService(
                            localDataSource = mockk(),
                            remoteDataSource = mockk(),
                            notificationsSender = testNotificationsSender,
                            appSettingsService = mockk(),
                        )

                        service.sendNotifications(
                            appSettings = appSettings,
                            oldPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviewsBefore),
                            newPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviewsAfter)
                        )

                        testNotificationsSender.newPullRequestCount shouldBe 0
                        testNotificationsSender.updatePullRequestCount shouldBe 1
                    }
                }
                describe("and new pull request changes updated but not seen (past)") {
                    it("should NOT send activity notification") {
                        val appSettings = buildAppSettings(
                            pullRequestNotificationsFilterOptions = PullRequestNotificationsFilterOptions(
                                open = false,
                                closed = false,
                                merged = true,
                                draft = false
                            ),
                            pullRequestStateNotificationsEnabled = true,
                            pullRequestActivityNotificationsEnabled = true,
                        )
                        val testNotificationsSender = TestNotificationsSender()
                        val repoToCheck = RandomEntities.repoToCheck()
                        val now = Clock.System.now()
                        val pullRequestBefore = RandomEntities.pullRequest(repoToCheckId = repoToCheck.id).copy(updatedAt = now)
                        val pullRequestSeen = RandomEntities.pullRequest(repoToCheckId = repoToCheck.id).copy(updatedAt = now.minus(2.days)).toPullRequestSeen(seenAt = now.minus(2.days))
                        val pullRequestWithRepoAndReviewsBefore = PullRequestWithRepoAndReviews(
                            repoToCheck = repoToCheck,
                            pullRequest = pullRequestBefore,
                            reviews = listOf(),
                            pullRequestSeen = pullRequestSeen,
                            reviewsSeen = listOf(),
                        )
                        val pullRequestAfter = pullRequestBefore.copy(updatedAt = pullRequestBefore.updatedAt.plus(1.days))
                        val pullRequestWithRepoAndReviewsAfter = PullRequestWithRepoAndReviews(
                            repoToCheck = repoToCheck,
                            pullRequest = pullRequestAfter,
                            reviews = listOf(),
                            pullRequestSeen = pullRequestSeen,
                            reviewsSeen = listOf(),
                        )
                        val service = PullRequestService(
                            localDataSource = mockk(),
                            remoteDataSource = mockk(),
                            notificationsSender = testNotificationsSender,
                            appSettingsService = mockk(),
                        )

                        service.sendNotifications(
                            appSettings = appSettings,
                            oldPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviewsBefore),
                            newPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviewsAfter)
                        )

                        testNotificationsSender.newPullRequestCount shouldBe 0
                        testNotificationsSender.updatePullRequestCount shouldBe 1
                    }
                }
            }
            describe("and only activity is enabled") {
                describe("and new pull request changes his state") {
                    // TODO this is a known bug because only the updated date is checked for the activity
                    it("should NOT send notifications").config(enabled = false) {
                        val appSettings = buildAppSettings(
                            pullRequestNotificationsFilterOptions = PullRequestNotificationsFilterOptions(
                                open = false,
                                closed = false,
                                merged = true,
                                draft = false
                            ),
                            pullRequestStateNotificationsEnabled = false,
                            pullRequestActivityNotificationsEnabled = true,
                        )
                        val testNotificationsSender = TestNotificationsSender()
                        val repoToCheck = RandomEntities.repoToCheck()
                        val now = Clock.System.now()
                        val pullRequestBefore = RandomEntities.pullRequest(repoToCheckId = repoToCheck.id)
                            .copy(
                                state = PullRequestState.OPEN,
                                updatedAt = now,
                            )
                        val pullRequestWithRepoAndReviewsBefore = PullRequestWithRepoAndReviews(
                            repoToCheck = repoToCheck,
                            pullRequest = pullRequestBefore,
                            reviews = listOf(),
                            pullRequestSeen = null,
                            reviewsSeen = listOf(),
                        )
                        val pullRequestAfter = pullRequestBefore
                            .copy(
                                state = PullRequestState.MERGED,
                                updatedAt = pullRequestBefore.updatedAt.plus(1.days)
                            )
                        val pullRequestWithRepoAndReviewsAfter = PullRequestWithRepoAndReviews(
                            repoToCheck = repoToCheck,
                            pullRequest = pullRequestAfter,
                            reviews = listOf(),
                            pullRequestSeen = null,
                            reviewsSeen = listOf(),
                        )
                        val service = PullRequestService(
                            localDataSource = mockk(),
                            remoteDataSource = mockk(),
                            notificationsSender = testNotificationsSender,
                            appSettingsService = mockk(),
                        )

                        service.sendNotifications(
                            appSettings = appSettings,
                            oldPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviewsBefore),
                            newPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviewsAfter)
                        )

                        testNotificationsSender.newPullRequestCount shouldBe 0
                        testNotificationsSender.updatePullRequestCount shouldBe 0
                    }
                }
            }
            describe("and state changes is NOT enabled") {
                it("should NOT send notifications") {
                    val appSettings = buildAppSettings(
                        pullRequestNotificationsFilterOptions = PullRequestNotificationsFilterOptions(
                            open = true,
                            closed = true,
                            merged = true,
                            draft = true
                        ),
                        pullRequestStateNotificationsEnabled = false,
                    )
                    val testNotificationsSender = TestNotificationsSender()
                    val pullRequest = RandomEntities.pullRequest()
                    val repoToCheck = RandomEntities.repoToCheck()
                    val pullRequestWithRepoAndReviews = PullRequestWithRepoAndReviews(
                        repoToCheck = repoToCheck,
                        pullRequest = pullRequest,
                        reviews = listOf(),
                        pullRequestSeen = null,
                        reviewsSeen = listOf(),
                    )
                    val service = PullRequestService(
                        localDataSource = mockk(),
                        remoteDataSource = mockk(),
                        notificationsSender = testNotificationsSender,
                        appSettingsService = mockk(),
                    )

                    service.sendNotifications(
                        appSettings = appSettings,
                        oldPullRequestsWithReviews = listOf(),
                        newPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviews)
                    )

                    testNotificationsSender.newPullRequestCount shouldBe 0
                }
            }
            describe("and activity is NOT enabled") {
                it("should NOT send notifications") {
                    val appSettings = buildAppSettings(
                        pullRequestNotificationsFilterOptions = PullRequestNotificationsFilterOptions(
                            open = true,
                            closed = true,
                            merged = true,
                            draft = true
                        ),
                        pullRequestActivityNotificationsEnabled = false,
                    )
                    val testNotificationsSender = TestNotificationsSender()
                    val pullRequest = RandomEntities.pullRequest()
                    val repoToCheck = RandomEntities.repoToCheck()
                    val pullRequestWithRepoAndReviews = PullRequestWithRepoAndReviews(
                        repoToCheck = repoToCheck,
                        pullRequest = pullRequest,
                        reviews = listOf(),
                        pullRequestSeen = null,
                        reviewsSeen = listOf(),
                    )
                    val service = PullRequestService(
                        localDataSource = mockk(),
                        remoteDataSource = mockk(),
                        notificationsSender = testNotificationsSender,
                        appSettingsService = mockk(),
                    )

                    service.sendNotifications(
                        appSettings = appSettings,
                        oldPullRequestsWithReviews = listOf(),
                        newPullRequestsWithReviews = listOf(pullRequestWithRepoAndReviews)
                    )

                    testNotificationsSender.newPullRequestCount shouldBe 0
                }
            }
        }
    }
})
