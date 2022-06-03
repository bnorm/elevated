package dev.bnorm.elevated.service.mongo

import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.Update.BitwiseOperatorBuilder
import org.springframework.data.mongodb.core.query.toPath
import java.time.Instant
import java.time.LocalDate
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

fun <T : Any> updateOf(builder: UpdateBuilder<T>.() -> Unit): Update =
    UpdateBuilder<T>().apply(builder).build()

class UpdateBuilder<T> {
    private val update = Update()
    fun build(): Update = update

    fun <E : Any> patch(property: KProperty1<T, E>, value: E?) {
        if (value != null) set(property, value)
    }

    fun <E : Any> set(property: KProperty1<T, E>, value: E?) {
        update.set(property.toPath(), value)
    }

    fun <E : Any> setOnInsert(property: KProperty1<T, E>, value: E?) {
        update.setOnInsert(property.toPath(), value)
    }

    fun <E : Any> unset(property: KProperty1<T, E>) {
        update.unset(property.toPath())
    }

    fun inc(property: KProperty1<T, Number>, inc: Number = 1) {
        update.inc(property.toPath(), inc)
    }

    fun <E : Any> push(property: KProperty1<T, E>): Update.PushOperatorBuilder =
        update.push(property.toPath())

    fun <E : Any> addToSet(property: KProperty1<T, E>): Update.AddToSetBuilder =
        update.addToSet(property.toPath())

    fun <E : Any> pop(property: KProperty1<T, E>, pos: Update.Position) {
        update.pop(property.toPath(), pos)
    }

    fun <E : Any> pull(property: KProperty1<T, E>, value: E?) {
        update.pull(property.toPath(), value)
    }

    fun <E : Any> pullAll(property: KProperty1<T, E>, values: Array<out E>) {
        update.pullAll(property.toPath(), values)
    }

    fun currentDate(property: KProperty1<T, LocalDate>) {
        update.currentDate(property.toPath())
    }

    fun currentTimestamp(property: KProperty1<T, Instant>) {
        update.currentTimestamp(property.toPath())
    }

    fun multiply(property: KProperty1<T, Number>, multiplier: Number) {
        update.multiply(property.toPath(), multiplier)
    }

    fun <E : Any> max(property: KProperty1<T, E>, value: E) {
        update.max(property.toPath(), value)
    }

    fun <E : Any> min(property: KProperty1<T, E>, value: E) {
        update.min(property.toPath(), value)
    }

    fun bitwise(property: KProperty1<T, Number>): BitwiseOperatorBuilder =
        update.bitwise(property.toPath())
}

fun Update.patch(property: KProperty<*>, value: Any?): Update =
    if (value != null) set(property.toPath(), value) else this

fun Update.set(property: KProperty<*>, value: Any?): Update =
    set(property.toPath(), value)

fun Update.setOnInsert(property: KProperty<*>, value: Any?): Update =
    setOnInsert(property.toPath(), value)

fun Update.unset(property: KProperty<*>): Update =
    unset(property.toPath())

fun Update.inc(property: KProperty<*>, inc: Number = 1): Update =
    inc(property.toPath(), inc)

fun Update.push(property: KProperty<*>): Update.PushOperatorBuilder =
    push(property.toPath())

fun Update.addToSet(property: KProperty<*>): Update.AddToSetBuilder =
    addToSet(property.toPath())

fun Update.pop(property: KProperty<*>, pos: Update.Position): Update =
    pop(property.toPath(), pos)

fun Update.pull(property: KProperty<*>, value: Any?): Update =
    pull(property.toPath(), value)

fun Update.pullAll(property: KProperty<*>, values: Array<out Any>): Update =
    pullAll(property.toPath(), values)

fun Update.currentDate(property: KProperty<*>): Update =
    currentDate(property.toPath())

fun Update.currentTimestamp(property: KProperty<*>): Update =
    currentTimestamp(property.toPath())

fun Update.multiply(property: KProperty<*>, multiplier: Number): Update =
    multiply(property.toPath(), multiplier)

fun Update.max(property: KProperty<*>, value: Any): Update =
    max(property.toPath(), value)

fun Update.min(property: KProperty<*>, value: Any): Update =
    min(property.toPath(), value)

fun Update.bitwise(property: KProperty<*>): BitwiseOperatorBuilder =
    bitwise(property.toPath())
