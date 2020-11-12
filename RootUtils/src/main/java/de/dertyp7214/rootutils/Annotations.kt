package de.dertyp7214.rootutils

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class RequireRoot(val throwError: Boolean = false, val message: String = "Root is required")