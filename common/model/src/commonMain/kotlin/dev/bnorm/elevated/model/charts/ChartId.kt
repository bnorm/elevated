package dev.bnorm.elevated.model.charts

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class ChartId(
    val value: String,
)
