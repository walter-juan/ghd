package com.woowla.ghd.data.local.db.utils

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

/**
 * Identity table with string as primary key
 *
 * @param name table name, by default name will be resolved from a class name with "Table" suffix removed (if present)
 * @param columnName name for a primary key, "id" by default
 */
open class TextIdTable(name: String = "", columnName: String = "id") : IdTable<String>(name) {
    final override val id: Column<EntityID<String>> = text(columnName).entityId()
    final override val primaryKey = PrimaryKey(id)
}

abstract class TextEntity(id: EntityID<String>) : Entity<String>(id)

abstract class TextEntityClass<out E : TextEntity>(
    table: IdTable<String>,
    entityType: Class<E>? = null,
    entityCtor: ((EntityID<String>) -> E)? = null
) : EntityClass<String, E>(table, entityType, entityCtor)