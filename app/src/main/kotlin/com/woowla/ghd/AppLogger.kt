package com.woowla.ghd

import org.slf4j.LoggerFactory

object AppLogger {
    private val logger = LoggerFactory.getLogger(AppLogger::class.java)

    fun d(msg: String) {
        logger.debug(msg)
    }

    fun e(msg: String, th: Throwable) {
        logger.error(msg, th)
    }
}