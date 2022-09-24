package dev.bnorm.elevated.work

import androidx.work.Constraints
import androidx.work.WorkRequest

fun buildConstraints(builder: Constraints.Builder.() -> Unit): Constraints =
    Constraints.Builder().apply(builder).build()

fun WorkRequest.Builder<*, *>.constraints(builder: Constraints.Builder.() -> Unit) {
    setConstraints(buildConstraints(builder))
}
