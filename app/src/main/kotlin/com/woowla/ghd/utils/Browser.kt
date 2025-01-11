package com.woowla.ghd.utils

import com.woowla.ghd.AppLogger
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.URISyntaxException
import java.net.URL

fun openFolder(file: File): Boolean {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    if (desktop != null && desktop.isSupported(Desktop.Action.OPEN)) {
        try {
            desktop.open(file)
            return true
        } catch (e: Exception) {
            AppLogger.e(e.message ?: "Error opening the file", e)
        }
    }
    return false
}

fun openWebpage(uri: URI): Boolean {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
        try {
            desktop.browse(uri)
            return true
        } catch (e: Exception) {
            AppLogger.e(e.message ?: "Error opening the webpage", e)
        }
    }
    return false
}

fun openWebpage(url: String): Boolean {
    try {
        return openWebpage(URI(url))
    } catch (e: URISyntaxException) {
        AppLogger.e(e.message ?: "Error opening the webpage", e)
    }
    return false
}

fun openWebpage(url: URL): Boolean {
    try {
        return openWebpage(url.toURI())
    } catch (e: URISyntaxException) {
        AppLogger.e(e.message ?: "Error opening the webpage", e)
    }
    return false
}