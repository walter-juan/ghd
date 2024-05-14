package com.woowla.ghd.data.local.prop

import com.woowla.ghd.AppFolderFactory
import com.woowla.ghd.data.local.prop.utils.BooleanProperty
import com.woowla.ghd.data.local.prop.utils.BooleanPropertyOrDefault
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.io.path.createFile

object AppProperties {
    private const val propName = "ghd.properties"
    private const val propFolder = "prop"
    private val propFolderPath by lazy {
        AppFolderFactory.folder.resolve(propFolder)
    }
    private val propertiesPath by lazy { propFolderPath.resolve(propName) }

    private val properties: Properties by lazy {
        createFile()
        Properties()
    }

    var darkTheme: Boolean? by BooleanProperty(properties, "darkTheme")
    var newPullRequestsNotificationsEnabled: Boolean by BooleanPropertyOrDefault(properties, "newPullRequestsNotificationsEnabled", true)
    var updatedPullRequestsNotificationsEnabled: Boolean by BooleanPropertyOrDefault(properties, "updatedPullRequestsNotificationsEnabled", true)
    var newReleaseNotificationsEnabled: Boolean by BooleanPropertyOrDefault(properties, "newReleaseNotificationsEnabled", true)
    var updatedReleaseNotificationsEnabled: Boolean by BooleanPropertyOrDefault(properties, "updatedReleaseNotificationsEnabled", true)

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