package com.woowla.ghd.core

import org.slf4j.Logger

class AppLogger(private val logger: Logger) {
    fun d(msg: String) {
        logger.debug(msg)
    }

    fun e(msg: String, th: Throwable) {
        logger.error(msg, th)
    }
}