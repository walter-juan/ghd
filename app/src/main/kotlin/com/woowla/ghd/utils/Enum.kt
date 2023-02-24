package com.woowla.ghd.utils

/**
 * Returns an enum entry with specified name, or [defaultValue] if the specified name does not match any of the enum constants defined in the class..
 * @see enumValueOf
 * @see enumValueOfOrElse
 */
inline fun <reified T : Enum<T>> enumValueOfOrDefault(name: String?, defaultValue: T): T {
    return enumValueOfOrElse(name) { defaultValue }
}

/**
 * Returns an enum entry with specified name, or the result of [defaultValue] function if the specified name does not match any of the enum constants defined in the class..
 * @see enumValueOf
 * @see enumValueOfOrDefault
 */
inline fun <reified T : Enum<T>> enumValueOfOrElse(name: String?, defaultValue: () -> T): T {
    return if (name == null) {
        defaultValue.invoke()
    } else {
        try {
            enumValueOf(name)
        } catch (ex: IllegalArgumentException) {
            defaultValue.invoke()
        }
    }
}