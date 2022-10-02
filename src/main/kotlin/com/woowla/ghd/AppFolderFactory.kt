package com.woowla.ghd

import java.nio.file.FileSystems
import java.nio.file.Paths
import net.harawata.appdirs.AppDirsFactory

object AppFolderFactory {
    val folder: String by lazy { createDir() }

    private fun createDir(): String {
        val path = if (BuildConfig.DEBUG) {
            Paths.get(FileSystems.getDefault().getPath("").toAbsolutePath().toString(), "ghd-debug")
        } else {
            val appDirs = AppDirsFactory.getInstance()
            val pathStr = appDirs.getUserDataDir("ghd", null, "woowla")
            Paths.get(pathStr)
        }
        val file = path.toFile()
        if (!file.exists()) {
            file.mkdirs()
        }
        return path.toString()
    }
}