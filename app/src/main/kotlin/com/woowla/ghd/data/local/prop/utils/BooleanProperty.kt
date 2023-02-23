package com.woowla.ghd.data.local.prop.utils

import java.util.*
import kotlin.reflect.KProperty

class BooleanProperty(private val properties: Properties, private val name: String) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean? {
        return properties.getProperty(name)?.toBooleanStrictOrNull()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean?) {
        properties.setProperty(name, value?.toString() ?: "")
    }
}