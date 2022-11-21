package com.woowla.ghd.domain.mappers

import com.woowla.ghd.domain.entities.SyncSettings
import org.mapstruct.Named

@Named("CheckTimeout")
class CheckTimeoutMapper {
    @Named("CheckTimeoutToValidCheckTimeout")
    fun checkTimeoutToValidCheckTimeout(checkTimeout: Long?): Long {
        return SyncSettings.getValidCheckTimeout(checkTimeout)
    }
}