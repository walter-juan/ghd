package com.woowla.ghd.data.local.prop

import com.russhwolf.settings.*
import com.woowla.ghd.AppFolderFactory
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.io.path.createFile

/**
 * This object is responsible for managing the properties of the application.
 * Old properties that can exits in this file:
 * - pullRequestNotificationsFilterOptionsOpen
 * - pullRequestNotificationsFilterOptionsClosed
 * - pullRequestNotificationsFilterOptionsMerged
 * - pullRequestNotificationsFilterOptionsDraft
 * - newPullRequestsNotificationsEnabled
 * - updatedPullRequestsNotificationsEnabled
 * - newReleaseNotificationsEnabled
 * - updatedReleaseNotificationsEnabled
 */
class AppProperties {
    companion object {
        private const val PROPERTIES_NAME = "ghd.properties"
        private const val PROPERTIES_FOLDER = "prop"
    }
    private val propFolderPath by lazy {
        AppFolderFactory.folder.resolve(PROPERTIES_FOLDER)
    }
    private val propertiesPath by lazy { propFolderPath.resolve(PROPERTIES_NAME) }

    private val properties: Properties by lazy {
        createFile()
        Properties()
    }

    val settings: Settings = PropertiesSettings(properties)

    var darkTheme: Boolean? by settings.nullableBoolean("darkTheme")

    var filtersPullRequestState: String? by settings.nullableString("filtersPullRequestState")
    var filtersReleaseGroupName: String? by settings.nullableString("filtersReleaseGroupName")
    var filtersRepoToCheckGroupName: String? by settings.nullableString("filtersRepoToCheckGroupName")

    var notificationsFilterUsername: String by settings.string("notificationsFilterUsername", "")
    var notificationsStateEnabledOption: String? by settings.nullableString("notificationsStateEnabledOption")
    var notificationsStateOpenFromOthersPullRequestsEnabled: Boolean by settings.boolean("notificationsStateOpenFromOthersPullRequestsEnabled", false)
    var notificationsStateClosedFromOthersPullRequestsEnabled: Boolean by settings.boolean("notificationsStateClosedFromOthersPullRequestsEnabled", false)
    var notificationsStateMergedFromOthersPullRequestsEnabled: Boolean by settings.boolean("notificationsStateMergedFromOthersPullRequestsEnabled", false)
    var notificationsStateDraftFromOthersPullRequestsEnabled: Boolean by settings.boolean("notificationsStateDraftFromOthersPullRequestsEnabled", false)
    var notificationsActivityEnabledOption: String? by settings.nullableString("notificationsActivityEnabledOption")
    var notificationsActivityReviewsFromYourPullRequestsEnabled: Boolean by settings.boolean("notificationsActivityReviewsFromYourPullRequestsEnabled", false)
    var notificationsActivityReviewsReRequestEnabled: Boolean by settings.boolean("notificationsActivityReviewsReRequestEnabled", false)
    var notificationsActivityChecksFromYourPullRequestsEnabled: Boolean by settings.boolean("notificationsActivityChecksFromYourPullRequestsEnabled", false)
    var notificationsActivityMergeableFromYourPullRequestsEnabled: Boolean by settings.boolean("notificationsActivityMergeableFromYourPullRequestsEnabled", false)
    var notificationsNewReleaseEnabled: Boolean by settings.boolean("notificationsNewReleaseEnabled", true)

    fun load() {
        FileInputStream(propertiesPath.toString()).use {
            properties.load(it)
        }
    }

    fun store() {
        FileOutputStream(propertiesPath.toString()).use {
            properties.store(it, null)
        }
    }

    private fun createFile() {
        val folder = propFolderPath.toFile()
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = propertiesPath.toFile()
        if (!file.exists()) {
            propertiesPath.createFile()
        }
    }
}