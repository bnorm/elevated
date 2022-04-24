package dev.bnorm.elevated.web

import org.w3c.dom.Storage
import kotlin.reflect.KProperty

inline operator fun Storage.getValue(thisRef: Any?, property: KProperty<*>): String? {
    return getItem(property.name)
}

inline operator fun Storage.setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
    if (value == null) {
        removeItem(property.name)
    } else {
        setItem(property.name, value)
    }
}
