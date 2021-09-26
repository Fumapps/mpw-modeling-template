package de.unistuttgart.iste.sqa.modeling.setup.util

fun String.toFirstUpper() =
    if (isNotEmpty()) this[0].toUpperCase() + substring(1) else this