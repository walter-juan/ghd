package com.woowla.ghd.data.local.prop

import com.woowla.ghd.AppFolderFactory
import com.woowla.ghd.data.local.prop.utils.BooleanProperty
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

    private val properties = Properties()

    var darkTheme: Boolean? by BooleanProperty(properties, "darkTheme")
    var featurePreviewNewCards: Boolean? by BooleanProperty(properties, "featurePreviewNewCards")
    var featurePreviewNewCardsBoldStyle: Boolean? by BooleanProperty(properties, "featurePreviewNewCardsBoldStyle")

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

    suspend fun createFile() {
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