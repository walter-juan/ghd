package com.woowla.ghd.domain.services

import com.woowla.ghd.RandomEntities
import com.woowla.ghd.TestNotificationsSender
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class ReleaseServiceUnitTest : StringSpec({
    "when newReleaseEnabled disabled then sendNotifications should NOT send any notifications" {
        // Given
        val testNotificationSender = TestNotificationsSender()
        val releaseService = ReleaseServiceImpl(
            localDataSource = mockk(),
            remoteDataSource = mockk(),
            notificationsSender = testNotificationSender,
            appSettingsService = mockk(),
            appLogger = mockk(relaxed = true),
        )
        val appSettings = RandomEntities.appSettings().copy(
            notificationsSettings = RandomEntities.notificationsSettings().copy(
                newReleaseEnabled = false
            )
        )

        val repoToCheck = RandomEntities.repoToCheck().copy(
            areReleasesNotificationsEnabled = true,
        )
        val release = RandomEntities.releaseWithRepo(repoToCheck)

        // When
        releaseService.sendNotifications(
            appSettings = appSettings,
            oldReleases = listOf(),
            newReleases = listOf(release),
        )

        // Then
        testNotificationSender.newReleaseCount shouldBe 0
    }

    "when newReleaseEnabled enabled then sendNotifications should send notifications" {
        // Given
        val testNotificationSender = TestNotificationsSender()
        val releaseService = ReleaseServiceImpl(
            localDataSource = mockk(),
            remoteDataSource = mockk(),
            notificationsSender = testNotificationSender,
            appSettingsService = mockk(),
            appLogger = mockk(relaxed = true),
        )
        val appSettings = RandomEntities.appSettings().copy(
            notificationsSettings = RandomEntities.notificationsSettings().copy(
                newReleaseEnabled = true
            )
        )

        val repoToCheck = RandomEntities.repoToCheck().copy(
            areReleasesNotificationsEnabled = true,
        )
        val release = RandomEntities.releaseWithRepo(repoToCheck)

        // When
        releaseService.sendNotifications(
            appSettings = appSettings,
            oldReleases = listOf(),
            newReleases = listOf(release),
        )

        // Then
        testNotificationSender.newReleaseCount shouldBe 1
    }

    "when newReleaseEnabled enabled and old and new releases are empty then sendNotifications should not send any notifications" {
        // Given
        val testNotificationSender = TestNotificationsSender()
        val releaseService = ReleaseServiceImpl(
            localDataSource = mockk(),
            remoteDataSource = mockk(),
            notificationsSender = testNotificationSender,
            appSettingsService = mockk(),
            appLogger = mockk(relaxed = true),
        )
        val appSettings = RandomEntities.appSettings().copy(
            notificationsSettings = RandomEntities.notificationsSettings().copy(
                newReleaseEnabled = true
            )
        )

        // When
        releaseService.sendNotifications(
            appSettings = appSettings,
            oldReleases = listOf(),
            newReleases = listOf(),
        )

        // Then
        testNotificationSender.newReleaseCount shouldBe 0
    }

    "when newReleaseEnabled enabled and old and new releases are the same then sendNotifications should not send any notifications" {
        // Given
        val testNotificationSender = TestNotificationsSender()
        val releaseService = ReleaseServiceImpl(
            localDataSource = mockk(),
            remoteDataSource = mockk(),
            notificationsSender = testNotificationSender,
            appSettingsService = mockk(),
            appLogger = mockk(relaxed = true),
        )
        val appSettings = RandomEntities.appSettings().copy(
            notificationsSettings = RandomEntities.notificationsSettings().copy(
                newReleaseEnabled = true
            )
        )

        val repoToCheck = RandomEntities.repoToCheck().copy(
            areReleasesNotificationsEnabled = true,
        )
        val release = RandomEntities.releaseWithRepo(repoToCheck)

        // When
        releaseService.sendNotifications(
            appSettings = appSettings,
            oldReleases = listOf(release),
            newReleases = listOf(release),
        )

        // Then
        testNotificationSender.newReleaseCount shouldBe 0
    }
})