package dev.bnorm.elevated.service.mongo

import dev.bnorm.elevated.model.Optional
import dev.bnorm.elevated.model.ifPresent
import java.time.Instant
import java.time.LocalDate
import kotlin.reflect.KProperty1
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.Update.BitwiseOperatorBuilder

fun <T : Any> Update(builder: UpdateBuilder<T>.() -> Unit): Update =
    UpdateBuilder<T>().apply(builder).build()

class UpdateBuilder<T> {
    private val update = Update()
    fun build(): Update = update

    fun <E> patch(property: KProperty1<T, E>, value: E?) {
        if (value != null) set(property, value)
    }

    fun <E> patch(property: KProperty1<T, E>, value: Optional<E>) {
        value.ifPresent { set(property, it) }
    }

    fun <E> set(property: KProperty1<T, E>, value: E?) {
        update.set(property.toDotPath(), value)
    }

    fun <E> setOnInsert(property: KProperty1<T, E>, value: E?) {
        update.setOnInsert(property.toDotPath(), value)
    }

    fun <E> unset(property: KProperty1<T, E>) {
        update.unset(property.toDotPath())
    }

    fun inc(property: KProperty1<T, Number>, inc: Number = 1) {
        update.inc(property.toDotPath(), inc)
    }

    fun <E> push(property: KProperty1<T, E>): Update.PushOperatorBuilder =
        update.push(property.toDotPath())

    fun <E> addToSet(property: KProperty1<T, E>): Update.AddToSetBuilder =
        update.addToSet(property.toDotPath())

    fun <E> pop(property: KProperty1<T, E>, pos: Update.Position) {
        update.pop(property.toDotPath(), pos)
    }

    fun <E> pull(property: KProperty1<T, E>, value: E?) {
        update.pull(property.toDotPath(), value)
    }

    fun <E> pullAll(property: KProperty1<T, E>, values: Array<out E>) {
        update.pullAll(property.toDotPath(), values)
    }

    fun currentDate(property: KProperty1<T, LocalDate>) {
        update.currentDate(property.toDotPath())
    }

    fun currentTimestamp(property: KProperty1<T, Instant>) {
        update.currentTimestamp(property.toDotPath())
    }

    fun multiply(property: KProperty1<T, Number>, multiplier: Number) {
        update.multiply(property.toDotPath(), multiplier)
    }

    fun <E : Any> max(property: KProperty1<T, E>, value: E) {
        update.max(property.toDotPath(), value)
    }

    fun <E : Any> min(property: KProperty1<T, E>, value: E) {
        update.min(property.toDotPath(), value)
    }

    fun bitwise(property: KProperty1<T, Number>): BitwiseOperatorBuilder =
        update.bitwise(property.toDotPath())
}
