package com.woowla.ghd.data.local.db.utils

/**
 * Get an entity by its [id].
 * @param id The id of the entity
 * @return The entity that has this id
 * @throws IllegalArgumentException if the entity has not been found by [id]
 */
fun <T : TextEntity> TextEntityClass<T>.findByIdOrThrow(id: String): T {
    return requireNotNull(findById(id)) { "Not found by id [$id]" }
}

/**
 * Create a new entity if [id] is null or the entity wasn't found by [id], otherwise
 * it will update the identity with id as [id].
 * @param id The id of the entity
 */
fun <T : TextEntity> TextEntityClass<T>.upsertById(id: String?, init: T.() -> Unit): T {
    val dao = id?.let { findById(it) }
    return dao?.apply(init) ?: this@upsertById.new(id = id) { init.invoke(this@new) }
}