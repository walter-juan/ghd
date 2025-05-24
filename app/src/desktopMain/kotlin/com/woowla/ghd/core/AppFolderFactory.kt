package com.woowla.ghd.core

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import net.harawata.appdirs.AppDirsFactory

class AppFolderFactory(isDebug: Boolean, debugAppFolder: String) {
    val folder: Path = if (isDebug) {
        Paths.get(FileSystems.getDefault().getPath("").toAbsolutePath().parent.toString(), debugAppFolder)
    } else {
        val appDirs = AppDirsFactory.getInstance()
        val pathStr = appDirs.getUserDataDir("ghd", null, "woowla")
        Paths.get(pathStr)
    }

    suspend fun createFolder() {
        val file = folder.toFile()
        if (!file.exists()) {
            file.mkdirs()
        }
    }
}