package com.woowla.ghd.domain.mappers

import com.woowla.ghd.domain.entities.AppSettings
import org.mapstruct.Named

@Named("CheckTimeout")
class CheckTimeoutMapper {
    @Named("CheckTimeoutToValidCheckTimeout")
    fun checkTimeoutToValidCheckTimeout(checkTimeout: Long?): Long {
        return AppSettings.getValidCheckTimeout(checkTimeout)
    }
}