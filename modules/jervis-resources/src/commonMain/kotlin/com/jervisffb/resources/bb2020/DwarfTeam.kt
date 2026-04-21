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
import com.jervisffb.engine.rules.common.skills.SkillType.BLOCK
import com.jervisffb.engine.rules.common.skills.SkillType.DAUNTLESS
import com.jervisffb.engine.rules.common.skills.SkillType.FRENZY
import com.jervisffb.engine.rules.common.skills.SkillType.SURE_HANDS
import com.jervisffb.engine.rules.common.skills.SkillType.TACKLE
import com.jervisffb.engine.rules.common.skills.SkillType.THICK_SKULL
import com.jervisffb.engine.serialize.RosterLogo
import com.jervisffb.engine.serialize.SingleSprite
import com.jervisffb.engine.serialize.SpriteSheet
import com.jervisffb.resources.iconRootPath
import com.jervisffb.resources.portraitRootPath

val DWARF_BLOCKER =
    RosterPosition(
        PositionId("dwarf-blocker"),
        12,
        "Dwarf Blocker Linemen",
        "Dwarf Blocker Lineman",
        "L",
        70_000,
        4, 3, 4, 5, 10,
        listOf(BLOCK.id(), TACKLE.id(), THICK_SKULL.id()),
        listOf(GENERAL, STRENGTH),
        listOf(AGILITY, PASSING),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/dwarf_blocker.png", 12),
        SingleSprite.ini("${portraitRootPath}/dwarf_blocker.png")
    )

val DWARF_RUNNER =
    RosterPosition(
        PositionId("dwarf-runner"),
        2,
        "Runners",
        "Runner",
        "R",
        85_000,
        6, 3, 3, 4, 9,
        listOf(SURE_HANDS.id(), THICK_SKULL.id()),
        listOf(GENERAL, AGILITY, PASSING),
        listOf(STRENGTH),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/dwarf_runner.png", 2),
        SingleSprite.ini("${portraitRootPath}/dwarf_runner.png")
    )

val DWARF_BLITZER =
    RosterPosition(
        PositionId("dwarf-blitzer"),
        2,
        "Blitzers",
        "Blitzer",
        "B",
        80_000,
        5, 3, 3, 4, 10,
        listOf(BLOCK.id(), THICK_SKULL.id()),
        listOf(GENERAL, STRENGTH),
        listOf(AGILITY, PASSING),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/dwarf_blitzer.png", 2),
        SingleSprite.ini("${portraitRootPath}/dwarf_blitzer.png")
    )

val DWARF_TROLL_SLAYER =
    RosterPosition(
        PositionId("dwarf-troll-slayer"),
        2,
        "Troll Slayers",
        "Troll Slayer",
        "TS",
        95_000,
        5, 3, 4, 0, 9,
        listOf(BLOCK.id(), DAUNTLESS.id(), FRENZY.id(), THICK_SKULL.id()),
        listOf(GENERAL, STRENGTH),
        listOf(AGILITY),
        emptyList(),
        emptyList(),
        PlayerSize.STANDARD,
        SpriteSheet.ini("${iconRootPath}/dwarf_trollslayer.png", 2),
        SingleSprite.ini("${portraitRootPath}/dwarf_trollslayer.png")
    )

val DWARF_TEAM_BB2020 = Roster(
    id = RosterId("jervis-dwarf"),
    name = "Dwarf",
    tier = 2,
    numberOfRerolls = 8,
    rerollCost = 50_000,
    allowApothecary = true,
    leagues = emptyList(),
    specialRules = listOf(RegionalSpecialRule.OLD_WORLD_CLASSIC),
    positions = listOf(
        DWARF_BLOCKER,
        DWARF_RUNNER,
        DWARF_BLITZER,
        DWARF_TROLL_SLAYER,
    ),
    logo = RosterLogo.NONE
)
