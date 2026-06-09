package com.jervisffb.engine.model.modifiers

import com.jervisffb.engine.commands.AddPlayerStatusEffect
import com.jervisffb.engine.model.Player
import com.jervisffb.engine.rules.common.skills.Duration
import kotlinx.serialization.Serializable

/**
 * Some effects are hard to put into other buckets, like a player that failed a
 * Blood Lust roll or a player that was added to the pitch through Spot The
 * Sneak. In these cases, we might want to mark the player somehow. This is done
 * through this enum and is added to players through [AddPlayerStatusEffect].
 *
 * It is up to the individual procedures to check for effects and react to them.
 * Effects are removed again as part of checking for any other modifiers that
 * has a [Duration].
 */
@Serializable
enum class PlayerStatusEffectType(val description: String) {
    // BB 2025
    ROOTED("Rooted"),
    DISTRACTED("Distracted"),
    CHOMPED("Chomped"),
    EYE_GOUGE("Eye Gouge"),
    // This is just a marker. Stat decreases are added separately
    DODGY_SNACK("Dodgy Snack"),

    // BB2020
    BONE_HEAD("Bone Head"), // Player failed a Bone Head roll
    REALLY_STUPID("Really Stupid"), // Player failed a Really Stupid Roll
    BLOOD_LUST("Blood Lust"), // Player failed a Blood Lust roll
    HYPNOTIC_GAZE("Hypnotic Gaze"), // Player is affected by Hypnotic Gaze
    UNCHANNELLED_FURY("Unchannelled Fury")
}

@Serializable
sealed interface PlayerStatusEffect {
    val type: PlayerStatusEffectType
    val duration: Duration

    companion object {
        fun chomped(causedBy: Player) = OwnedPlayerStatusEffect(PlayerStatusEffectType.CHOMPED, Duration.SPECIAL, causedBy)
        fun distracted() = SimplePlayerStatusEffect(PlayerStatusEffectType.DISTRACTED, Duration.START_OF_ACTIVATION)
        fun dodgySnack() = SimplePlayerStatusEffect(PlayerStatusEffectType.DODGY_SNACK, Duration.END_OF_DRIVE)
        fun eyeGouge() = SimplePlayerStatusEffect(PlayerStatusEffectType.EYE_GOUGE, Duration.START_OF_ACTIVATION)
        fun unchannelledFury() = SimplePlayerStatusEffect(PlayerStatusEffectType.UNCHANNELLED_FURY, Duration.START_OF_ACTIVATION)
        fun boneHead() = SimplePlayerStatusEffect(PlayerStatusEffectType.BONE_HEAD, Duration.START_OF_ACTIVATION)
        fun reallyStupid() = SimplePlayerStatusEffect(PlayerStatusEffectType.REALLY_STUPID, Duration.START_OF_ACTIVATION)
        fun bloodLust() = SimplePlayerStatusEffect(PlayerStatusEffectType.BLOOD_LUST, Duration.END_OF_ACTIVATION)
        // Will be removed at end-of-drive, unless manually removed before (by being knocked down or placed prone)
        fun rooted() = SimplePlayerStatusEffect(PlayerStatusEffectType.ROOTED, Duration.END_OF_DRIVE)
    }
}

data class SimplePlayerStatusEffect(
    override val type: PlayerStatusEffectType,
    override val duration: Duration
) : PlayerStatusEffect

data class OwnedPlayerStatusEffect(
    override val type: PlayerStatusEffectType,
    override val duration: Duration,
    val causedBy: Player,
) : PlayerStatusEffect
