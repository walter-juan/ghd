package com.woowla.ghd

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import net.harawata.appdirs.AppDirsFactory

object AppFolderFactory {
    val folder: Path = if (BuildConfig.DEBUG) {

        Paths.get(FileSystems.getDefault().getPath("").toAbsolutePath().parent.toString(), BuildConfig.DEBUG_APP_FOLDER)
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