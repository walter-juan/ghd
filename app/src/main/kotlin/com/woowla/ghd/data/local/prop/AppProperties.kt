package com.woowla.ghd.data.local.prop

import com.russhwolf.settings.*
import com.woowla.ghd.AppFolderFactory
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.io.path.createFile

object AppProperties {
    private const val PROPERTIES_NAME = "ghd.properties"
    private const val PROPERTIES_FOLDER = "prop"
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
    var newPullRequestsNotificationsEnabled: Boolean by settings.boolean("newPullRequestsNotificationsEnabled", true)
    var updatedPullRequestsNotificationsEnabled: Boolean by settings.boolean("updatedPullRequestsNotificationsEnabled", true)
    var newReleaseNotificationsEnabled: Boolean by settings.boolean("newReleaseNotificationsEnabled", true)
    var updatedReleaseNotificationsEnabled: Boolean by settings.boolean("updatedReleaseNotificationsEnabled", true)

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