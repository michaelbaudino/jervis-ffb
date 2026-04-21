package com.jervisffb.resources.bb2020

import com.jervisffb.engine.model.PlayerSize
import com.jervisffb.engine.model.PositionId
import com.jervisffb.engine.model.RosterId
import com.jervisffb.engine.rules.common.roster.RegionalSpecialRule
import com.jervisffb.engine.rules.common.roster.Roster
import com.jervisffb.engine.rules.common.roster.RosterPosition
import com.jervisffb.engine.rules.common.skills.SkillCategory.AGILITY
import com.jervisffb.engine.rules.common.skills.SkillCategory.GENERAL
import com.jervisffb.engine.rules.common.skills.SkillCategory.MUTATIONS
import com.jervisffb.engine.rules.common.skills.SkillCategory.PASSING
import com.jervisffb.engine.rules.common.skills.SkillCategory.STRENGTH
import com.jervisffb.engine.rules.common.skills.SkillType.ANIMOSITY
import com.jervisffb.engine.rules.common.skills.SkillType.ALWAYS_HUNGRY
import com.jervisffb.engine.rules.common.skills.SkillType.ANIMAL_SAVAGERY
import com.jervisffb.engine.rules.common.skills.SkillType.BONE_HEAD
import com.jervisffb.engine.rules.common.skills.SkillType.DECAY
import com.jervisffb.engine.rules.common.skills.SkillType.DODGE
import com.jervisffb.engine.rules.common.skills.SkillType.FRENZY
import com.jervisffb.engine.rules.common.skills.SkillType.HORNS
import com.jervisffb.engine.rules.common.skills.SkillType.LONER
import com.jervisffb.engine.rules.common.skills.SkillType.MIGHTY_BLOW
import com.jervisffb.engine.rules.common.skills.SkillType.PASS
import com.jervisffb.engine.rules.common.skills.SkillType.PLAGUE_RIDDEN
import com.jervisffb.engine.rules.common.skills.SkillType.PREHENSILE_TAIL
import com.jervisffb.engine.rules.common.skills.SkillType.PROJECTILE_VOMIT
import com.jervisffb.engine.rules.common.skills.SkillType.REALLY_STUPID
import com.jervisffb.engine.rules.common.skills.SkillType.REGENERATION
import com.jervisffb.engine.rules.common.skills.SkillType.RIGHT_STUFF
import com.jervisffb.engine.rules.common.skills.SkillType.SAFE_PAIR_OF_HANDS
import com.jervisffb.engine.rules.common.skills.SkillType.STUNTY
import com.jervisffb.engine.rules.common.skills.SkillType.THICK_SKULL
import com.jervisffb.engine.rules.common.skills.SkillType.THROW_TEAMMATE
import com.jervisffb.engine.rules.common.skills.SkillType.UNCHANNELLED_FURY
import com.jervisffb.engine.serialize.RosterLogo
import com.jervisffb.engine.serialize.SingleSprite
import com.jervisffb.engine.serialize.SpriteSheet
import com.jervisffb.resources.iconRootPath
import com.jervisffb.resources.portraitRootPath

val RENEGADE_HUMAN_LINEMAN =
    RosterPosition(
        PositionId("renegade-human-lineman"),
        12,
        "Renegade Human Linemen",
        "Renegade Human Lineman",
        "L",
        50_000,
        6, 3, 3, 4, 9,
        emptyList(),
        listOf(GENERAL, MUTATIONS),
        listOf(AGILITY, STRENGTH),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/renegade_human_lineman.png", 12),
        SingleSprite.ini("${portraitRootPath}/renegade_human_lineman.png")
    )

val RENEGADE_GOBLIN =
    RosterPosition(
        PositionId("renegade-goblin"),
        1,
        "Renegade Goblins",
        "Renegade Goblin",
        "G",
        40_000,
        6, 2, 3, 4, 8,
        listOf(ANIMOSITY.id(), DODGE.id(), RIGHT_STUFF.id(), STUNTY.id()),
        listOf(AGILITY, MUTATIONS),
        listOf(GENERAL, PASSING),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/renegade_goblin.png", 1),
        SingleSprite.ini("${portraitRootPath}/renegade_goblin.png")
    )

val RENEGADE_ORC =
    RosterPosition(
        PositionId("renegade-orc"),
        1,
        "Renegade Orcs",
        "Renegade Orc",
        "O",
        50_000,
        5, 3, 3, 5, 10,
        listOf(ANIMOSITY.id()),
        listOf(GENERAL, MUTATIONS),
        listOf(AGILITY, STRENGTH),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/renegade_orc.png", 1),
        SingleSprite.ini("${portraitRootPath}/renegade_orc.png")
    )

val RENEGADE_SKAVEN =
    RosterPosition(
        PositionId("renegade-skaven"),
        1,
        "Renegade Skaven",
        "Renegade Skaven",
        "S",
        50_000,
        7, 3, 3, 4, 8,
        listOf(ANIMOSITY.id()),
        listOf(GENERAL, MUTATIONS),
        listOf(AGILITY, STRENGTH),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/renegade_skaven.png", 1),
        SingleSprite.ini("${portraitRootPath}/renegade_skaven.png")
    )

val RENEGADE_HUMAN_THROWER =
    RosterPosition(
        PositionId("renegade-human-thrower"),
        1,
        "Renegade Human Throwers",
        "Renegade Human Thrower",
        "T",
        75_000,
        6, 3, 3, 3, 9,
        listOf(ANIMOSITY.id(), PASS.id(), SAFE_PAIR_OF_HANDS.id()),
        listOf(GENERAL, MUTATIONS, PASSING),
        listOf(AGILITY, STRENGTH),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/renegade_human_thrower.png", 1),
        SingleSprite.ini("${portraitRootPath}/renegade_human_thrower.png")
    )

val RENEGADE_DARK_ELF =
    RosterPosition(
        PositionId("renegade-dark-elf"),
        1,
        "Renegade Dark Elves",
        "Renegade Dark Elf",
        "E",
        75_000,
        6, 3, 2, 3, 9,
        listOf(ANIMOSITY.id()),
        listOf(GENERAL, AGILITY, MUTATIONS),
        listOf(STRENGTH, PASSING),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/renegade_dark_elf.png", 1),
        SingleSprite.ini("${portraitRootPath}/renegade_dark_elf.png")
    )

val RENEGADE_TROLL =
    RosterPosition(
        PositionId("renegade-troll"),
        1,
        "Renegade Trolls",
        "Renegade Troll",
        "T",
        115_000,
        4, 5, 5, 5, 10,
        listOf(ALWAYS_HUNGRY.id(), LONER.id(), MIGHTY_BLOW.id(), PROJECTILE_VOMIT.id(), REALLY_STUPID.id(), REGENERATION.id(), THROW_TEAMMATE.id()),
        listOf(STRENGTH),
        listOf(GENERAL, AGILITY, MUTATIONS),
        emptyList(),
        emptyList(),
        PlayerSize.BIG_GUY,
        SpriteSheet.ini("${iconRootPath}/renegade_troll.png", 1),
        SingleSprite.ini("${portraitRootPath}/renegade_troll.png")
    )

val RENEGADE_OGRE =
    RosterPosition(
        PositionId("renegade-ogre"),
        1,
        "Renegade Ogres",
        "Renegade Ogre",
        "O",
        140_000,
        5, 5, 4, 5, 10,
        listOf(BONE_HEAD.id(), LONER.id(), MIGHTY_BLOW.id(), THICK_SKULL.id(), THROW_TEAMMATE.id()),
        listOf(STRENGTH),
        listOf(GENERAL, AGILITY, MUTATIONS),
        emptyList(),
        emptyList(),
        PlayerSize.BIG_GUY,
        SpriteSheet.ini("${iconRootPath}/renegade_ogre.png", 1),
        SingleSprite.ini("${portraitRootPath}/renegade_ogre.png")
    )

val RENEGADE_MINOTAUR =
    RosterPosition(
        PositionId("renegade-minotaur"),
        1,
        "Renegade Minotaurs",
        "Renegade Minotaur",
        "M",
        150_000,
        5, 5, 4, null, 9,
        listOf(FRENZY.id(), HORNS.id(), LONER.id(), MIGHTY_BLOW.id(), THICK_SKULL.id(), UNCHANNELLED_FURY.id()),
        listOf(STRENGTH),
        listOf(GENERAL, AGILITY, MUTATIONS),
        emptyList(),
        emptyList(),
        PlayerSize.BIG_GUY,
        SpriteSheet.ini("${iconRootPath}/renegade_minotaur.png", 1),
        SingleSprite.ini("${portraitRootPath}/renegade_minotaur.png")
    )

val RENEGADE_RAT_OGRE =
    RosterPosition(
        PositionId("renegade-rat-ogre"),
        1,
        "Renegade Rat Ogres",
        "Renegade Rat Ogre",
        "R",
        150_000,
        6, 5, 4, null, 9,
        listOf(ANIMAL_SAVAGERY.id(), FRENZY.id(), LONER.id(), MIGHTY_BLOW.id(), PREHENSILE_TAIL.id()),
        listOf(STRENGTH),
        listOf(GENERAL, AGILITY, MUTATIONS),
        emptyList(),
        emptyList(),
        PlayerSize.BIG_GUY,
        SpriteSheet.ini("${iconRootPath}/renegade_rat_ogre.png", 1),
        SingleSprite.ini("${portraitRootPath}/renegade_rat_ogre.png")
    )

val ROTTER_LINEMAN =
    RosterPosition(
        PositionId("rotter-lineman"),
        16,
        "Rotter Linemen",
        "Rotter Lineman",
        "R",
        35_000,
        5, 3, 4, 6, 9,
        listOf(DECAY.id(), PLAGUE_RIDDEN.id()),
        listOf(GENERAL, MUTATIONS),
        listOf(AGILITY, STRENGTH),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/rotter_lineman.png", 16),
        SingleSprite.ini("${portraitRootPath}/rotter_lineman.png")
    )

val CHAOS_RENEGADE_TEAM_BB2020 = Roster(
    id = RosterId("jervis-chaos-renegade"),
    name = "Chaos Renegade",
    tier = 2,
    numberOfRerolls = 8,
    rerollCost = 70_000,
    allowApothecary = true,
    leagues = emptyList(),
    specialRules = listOf(RegionalSpecialRule.BADLANDS_BRAWL),
    positions = listOf(
        RENEGADE_HUMAN_LINEMAN,
        RENEGADE_GOBLIN,
        RENEGADE_ORC,
        RENEGADE_SKAVEN,
        RENEGADE_HUMAN_THROWER,
        RENEGADE_DARK_ELF,
        RENEGADE_TROLL,
        RENEGADE_OGRE,
        RENEGADE_MINOTAUR,
        RENEGADE_RAT_OGRE,
        ROTTER_LINEMAN,
    ),
    logo = RosterLogo.NONE
)
