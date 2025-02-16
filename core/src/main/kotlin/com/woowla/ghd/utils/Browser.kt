package com.woowla.ghd.utils

import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import kotlin.jvm.Throws

fun openFolder(file: File): Boolean {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    checkNotNull(desktop) { "Desktop is not supported" }
    check(desktop.isSupported(Desktop.Action.OPEN)) { "Desktop open action is not supported" }
    desktop.open(file)
    return false
}

fun openWebpage(uri: URI): Boolean {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    checkNotNull(desktop) { "Desktop is not supported" }
    check(desktop.isSupported(Desktop.Action.BROWSE)) { "Desktop browse action is not supported" }
    desktop.browse(uri)
    return false
}

@Throws(URISyntaxException::class)
fun openWebpage(url: String): Boolean {
    return openWebpage(URI(url))
}

@Throws(URISyntaxException::class)
fun openWebpage(url: URL): Boolean {
    return openWebpage(url.toURI())
}