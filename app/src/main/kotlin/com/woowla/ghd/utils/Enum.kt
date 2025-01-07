package com.woowla.ghd.utils

/**
 * Returns an enum entry with specified name, or [defaultValue] if the specified name does not match any of the enum constants defined in the class.
 */
inline fun <reified T : Enum<T>> enumValueOfOrDefault(name: String?, defaultValue: T): T {
    return enumValueOfOrElse(name) { defaultValue }
}

/**
 * Returns an enum entry with specified name, or the result of [defaultValue] function if the specified name does not match any of the enum constants defined in the class.
 */
inline fun <reified T : Enum<T>> enumValueOfOrElse(name: String?, defaultValue: () -> T): T {
    return enumValueOfOrNull<T>(name) ?: defaultValue.invoke()
}

/**
 * Returns an enum entry with specified name, or null if the specified name does not match any of the enum constants defined in the class.
 */
inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String?): T? {
    return try {
        enumValueOfOrThrow<T>(name)
    } catch (ex: IllegalArgumentException) {
        null
    }
}

/**
 * Returns an enum entry with specified name, or throw [IllegalArgumentException].
 * @throws IllegalArgumentException if the specified name does not match any of the enum constants defined in the class.
 */
inline fun <reified T : Enum<T>> enumValueOfOrThrow(name: String?): T {
    require(name != null) { "Name must not be null" }
    return enumValueOf(name)
}