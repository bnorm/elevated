package dev.bnorm.elevated.inject

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FIELD,
)
expect annotation class Inject()
