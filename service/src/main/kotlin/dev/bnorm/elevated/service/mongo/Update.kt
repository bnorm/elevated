package dev.bnorm.elevated.service.mongo

import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.Update.BitwiseOperatorBuilder
import org.springframework.data.mongodb.core.query.toPath
import kotlin.reflect.KProperty

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
