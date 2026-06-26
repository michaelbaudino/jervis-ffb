package com.jervisffb.engine.model.inducements

/**
 * This file is just being used for prototyping how to model inducements
 * It probably needs a few iterations
 */
enum class ApothecaryType(val description: String) {
    STANDARD("Apothecary"), // See page 62 in the BB2020 rulebook
    WANDERING("Wandering Apothecary"), // See page 91 in the BB2020 rulebook
    // See page 91 in the BB2020 rulebook
    // See page 145 in the BB2020 rulebook
    PLAGUE_DOCTOR("Plague Doctor")
}

/**
 * Class representing all types of apothecaries.
 */
data class Apothecary(
    var used: Boolean,
    val type: ApothecaryType
)

/**
 * Class representing a Mortuary Assistant.
 *
 * See page 91 in the BB2020 rulebook
 * See page 145 in the BB2025 rulebook
 */
data class MortuaryAssistant(var used: Boolean = false)
