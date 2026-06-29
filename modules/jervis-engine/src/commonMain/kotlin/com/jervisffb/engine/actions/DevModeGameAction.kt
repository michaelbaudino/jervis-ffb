package com.jervisffb.engine.actions

import com.jervisffb.engine.model.Ball
import com.jervisffb.engine.model.BallState
import com.jervisffb.engine.model.Game
import com.jervisffb.engine.model.Player
import com.jervisffb.engine.model.PlayerId
import com.jervisffb.engine.model.PlayerKeyword
import com.jervisffb.engine.model.PlayerState
import com.jervisffb.engine.model.SkillId
import com.jervisffb.engine.model.locations.PitchCoordinate
import com.jervisffb.engine.model.modifiers.StatModifier
import kotlinx.serialization.Serializable

/**
 * Interface for game actions that can modify the game state "out-of-order",
 * i.e., they can do things that you would normally not allow. This can be
 * useful during development or as an admin tool.
 *
 * The [com.jervisffb.engine.GameEngineController] should accept these in
 * [com.jervisffb.engine.GameEngineController.handleAction] and leave access
 * control to other layers.
 */
sealed interface DevModeGameAction: GameAction

@Serializable
data class ChangePlayerBaseStat(
    val playerId: PlayerId,
    val type: StatModifier.Type,
    val modifier: Int
): DevModeGameAction {
    fun getPlayer(state: Game): Player = state.getPlayerById(playerId)
}

@Serializable
data class AddPlayerSkill(
    val playerId: PlayerId,
    val skill: SkillId
): DevModeGameAction {
    fun getPlayer(state: Game): Player = state.getPlayerById(playerId)
}

@Serializable
data class RemovePlayerSkill(
    val playerId: PlayerId,
    val skill: SkillId
): DevModeGameAction {
    fun getPlayer(state: Game): Player = state.getPlayerById(playerId)
}

@Serializable
data class AddPlayerKeyword(
    val playerId: PlayerId,
    val keyword: PlayerKeyword
): DevModeGameAction {
    fun getPlayer(state: Game): Player = state.getPlayerById(playerId)
}

@Serializable
data class RemovePlayerKeyword(
    val playerId: PlayerId,
    val keyword: PlayerKeyword
): DevModeGameAction {
    fun getPlayer(state: Game): Player = state.getPlayerById(playerId)
}

@Serializable
data class SetPlayerLocation(
    val playerId: PlayerId,
    val x: Int,
    val y: Int
) : DevModeGameAction {
    fun getPlayer(state: Game): Player = state.getPlayerById(playerId)
    fun coordinate(): PitchCoordinate = PitchCoordinate(x, y)
}

@Serializable
data class SetPlayerState(
    val playerId: PlayerId,
    val state: PlayerState,
    val hasTackleZones: Boolean? = null
) : DevModeGameAction {
    fun getPlayer(state: Game): Player = state.getPlayerById(playerId)
}

@Serializable
data class SetBallLocation(
    val x: Int? = null,
    val y: Int? = null,
    val playerId: PlayerId? = null,
    val ballState: BallState? = null
) : DevModeGameAction {
    fun getBall(state: Game): Ball = state.balls.first()
    fun getPlayer(state: Game): Player? = playerId?.let { state.getPlayerById(it) }
    fun coordinate(): PitchCoordinate? {
        val cx = x ?: return null
        val cy = y ?: return null
        return PitchCoordinate(cx, cy)
    }
}
