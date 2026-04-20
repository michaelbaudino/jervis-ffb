package com.jervisffb.resources.bb2020

import com.jervisffb.engine.model.PlayerSize
import com.jervisffb.engine.model.PositionId
import com.jervisffb.engine.model.RosterId
import com.jervisffb.engine.rules.common.roster.RegionalSpecialRule
import com.jervisffb.engine.rules.common.roster.Roster
import com.jervisffb.engine.rules.common.roster.RosterPosition
import com.jervisffb.engine.rules.common.skills.SkillCategory.AGILITY
import com.jervisffb.engine.rules.common.skills.SkillCategory.GENERAL
import com.jervisffb.engine.rules.common.skills.SkillCategory.PASSING
import com.jervisffb.engine.rules.common.skills.SkillCategory.STRENGTH
import com.jervisffb.engine.rules.common.skills.SkillType.ANIMAL_SAVAGERY
import com.jervisffb.engine.rules.common.skills.SkillType.BLOCK
import com.jervisffb.engine.rules.common.skills.SkillType.BONE_HEAD
import com.jervisffb.engine.rules.common.skills.SkillType.CATCH
import com.jervisffb.engine.rules.common.skills.SkillType.FEND
import com.jervisffb.engine.rules.common.skills.SkillType.LONER
import com.jervisffb.engine.rules.common.skills.SkillType.MIGHTY_BLOW
import com.jervisffb.engine.rules.common.skills.SkillType.STAND_FIRM
import com.jervisffb.engine.rules.common.skills.SkillType.THICK_SKULL
import com.jervisffb.engine.rules.common.skills.SkillType.THROW_TEAMMATE
import com.jervisffb.engine.rules.common.skills.SkillType.WRESTLE
import com.jervisffb.engine.serialize.RosterLogo
import com.jervisffb.engine.serialize.SingleSprite
import com.jervisffb.engine.serialize.SpriteSheet
import com.jervisffb.resources.iconRootPath
import com.jervisffb.resources.portraitRootPath

val IMPERIAL_RETAINER_LINEMAN =
    RosterPosition(
        PositionId("imperial-nobility-lineman"),
        12,
        "Imperial Retainer Linemen",
        "Imperial Retainer Lineman",
        "L",
        45_000,
        6, 3, 3, 4, 9,
        listOf(FEND.id()),
        listOf(GENERAL),
        listOf(AGILITY, STRENGTH),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/imperialnoble_lineman.png", 12),
        SingleSprite.ini("${portraitRootPath}/imperialnoble_lineman.png")
    )

val IMPERIAL_BODYGUARD =
    RosterPosition(
        PositionId("imperial-nobility-bodyguard"),
        4,
        "Bodyguards",
        "Bodyguard",
        "Bg",
        90_000,
        5, 3, 3, 5, 10,
        listOf(STAND_FIRM.id(), WRESTLE.id()),
        listOf(GENERAL, STRENGTH),
        listOf(AGILITY),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/imperialnoble_bodyguard.png", 4),
        SingleSprite.ini("${portraitRootPath}/imperialnoble_bodyguard.png")
    )

val NOBLE_BLITZER =
    RosterPosition(
        PositionId("imperial-nobility-noble"),
        2,
        "Noble Blitzers",
        "Noble Blitzer",
        "N",
        105_000,
        7, 3, 3, 5, 9,
        listOf(BLOCK.id(), CATCH.id()),
        listOf(GENERAL, AGILITY),
        listOf(STRENGTH, PASSING),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/imperialnoble_nobleblitzer.png", 2),
        SingleSprite.ini("${portraitRootPath}/imperialnoble_nobleblitzer.png")
    )

val IMPERIAL_OGRE =
    RosterPosition(
        PositionId("imperial-nobility-ogre"),
        1,
        "Ogre",
        "Ogre",
        "O",
        140_000,
        5, 5, 3, 5, 9,
        listOf(
            BONE_HEAD.id(),
            LONER.id(5),
            MIGHTY_BLOW.id(1),
            THICK_SKULL.id(),
            THROW_TEAMMATE.id(),
        ),
        listOf(STRENGTH),
        listOf(GENERAL),
        emptyList(),
        emptyList(),
        PlayerSize.BIG_GUY,
        SpriteSheet.ini("${iconRootPath}/imperialnoble_ogre.png", 1),
        SingleSprite.ini("${portraitRootPath}/imperialnoble_ogre.png")
    )

val IMPERIAL_NOBILITY_TEAM_BB2020 = Roster(
    id = RosterId("jervis-imperial-nobility"),
    name = "Imperial Nobility",
    tier = 2,
    numberOfRerolls = 6,
    rerollCost = 70_000,
    allowApothecary = true,
    leagues = emptyList(),
    specialRules = listOf(RegionalSpecialRule.OLD_WORLD_CLASSIC),
    positions = listOf(
        IMPERIAL_RETAINER_LINEMAN,
        IMPERIAL_BODYGUARD,
        NOBLE_BLITZER,
        IMPERIAL_OGRE,
    ),
    logo = RosterLogo.NONE
)
