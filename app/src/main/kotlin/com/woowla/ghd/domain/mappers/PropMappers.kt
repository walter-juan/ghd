package com.woowla.ghd.domain.mappers

import com.woowla.ghd.data.local.prop.entities.PropAppSettings
import com.woowla.ghd.domain.entities.AppSettings
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface PropMappers {
    companion object {
        val INSTANCE: PropMappers = Mappers.getMapper(PropMappers::class.java)
    }

    fun propAppSettingsToAppSettings(propAppSettings: PropAppSettings): AppSettings
}