package com.woowla.ghd.data.local.db.utils

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

/**
 * Get an entity by its [id].
 * @param id The id of the entity
 * @return The entity that has this id
 * @throws IllegalArgumentException if the entity has not been found by [id]
 */
fun <T : LongEntity> LongEntityClass<T>.findByIdOrThrow(id: Long): T {
    return requireNotNull(findById(id)) { "Not found by id [$id]" }
}

/**
 * Create a new entity if [id] is null or the entity wasn't found by [id], otherwise
 * it will update the identity with id as [id].
 * @param id The id of the entity
 */
fun <T : LongEntity> LongEntityClass<T>.upsertById(id: Long?, init: T.() -> Unit): T {
    val dao = id?.let { findById(it) }
    return dao?.apply(init) ?: this@upsertById.new(id = id) { init.invoke(this@new) }
}