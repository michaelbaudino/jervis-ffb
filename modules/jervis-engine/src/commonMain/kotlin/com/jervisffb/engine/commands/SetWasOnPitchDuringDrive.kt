package com.jervisffb.engine.commands

import com.jervisffb.engine.model.Game
import com.jervisffb.engine.rules.bb2025.skills.SecretWeapon

class SetWasOnPitchDuringDrive(private val skill: SecretWeapon, private val onPitch: Boolean): Command {
    private var originalValue: Boolean = false
    override fun execute(state: Game) {
        originalValue = skill.onPitchDuringDrive
        skill.onPitchDuringDrive = onPitch
    }
    override fun undo(state: Game) {
        skill.onPitchDuringDrive = originalValue
    }
}
