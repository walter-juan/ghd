package com.woowla.ghd.domain.mappers

import org.jetbrains.exposed.dao.id.EntityID

class EntityIdMapper {
    fun longEntityIdToLong(value: EntityID<Long>): Long {
        return value.value
    }
    fun stringEntityIdToString(value: EntityID<String>): String {
        return value.value
    }
}